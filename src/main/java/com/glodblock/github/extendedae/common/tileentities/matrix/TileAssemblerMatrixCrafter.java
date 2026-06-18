package com.glodblock.github.extendedae.common.tileentities.matrix;

import appeng.api.config.Actionable;
import appeng.api.crafting.IPatternDetails;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageService;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.CombinedInternalInventory;
import appeng.util.inv.InternalInventoryHost;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.common.me.CraftingMatrixThread;
import com.glodblock.github.extendedae.common.me.CraftingThread;
import com.glodblock.github.extendedae.common.me.matrix.ClusterAssemblerMatrix;
import com.glodblock.github.glodium.util.GlodUtil;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class TileAssemblerMatrixCrafter extends TileAssemblerMatrixFunction implements InternalInventoryHost, IGridTickable {

    public static final int MAX_THREAD = 8;
    private static final int MAX_BUFF = 128;
    private static final int COOL_TIME = 5 * 20;
    private final CraftingThread[] threads = new CraftingThread[MAX_THREAD];
    private final InternalInventory internalInv;
    private final OutputBuffer outputBuffer;
    private short states = 0b000000;
    private int blockCoolDown = 0;

    public TileAssemblerMatrixCrafter(BlockPos pos, BlockState blockState) {
        super(GlodUtil.getTileType(TileAssemblerMatrixCrafter.class, TileAssemblerMatrixCrafter::new, EAESingletons.ASSEMBLER_MATRIX_CRAFTER), pos, blockState);
        this.getMainNode().addService(IGridTickable.class, this);
        this.outputBuffer = new OutputBuffer();
        var invs = new InternalInventory[MAX_THREAD];
        for (int x = 0; x < MAX_THREAD; x ++) {
            final int index = x;
            this.threads[index] = new CraftingMatrixThread(this, this::getSrc, signal -> this.changeState(index, signal));
            invs[index] = this.threads[index].getInternalInventory();
        }
        this.internalInv = new CombinedInternalInventory(invs);
    }

    private IActionSource getSrc() {
        return this.cluster.getSrc();
    }

    @SuppressWarnings("lossy-conversions")
    private void changeState(int index, boolean state) {
        boolean oldState = this.states > 0;
        if (state) {
            this.states |= (1 << index);
        } else {
            this.states &= ~(1 << index);
        }
        if (state) {
            if (!oldState) {
                this.getMainNode().ifPresent((grid, node) -> grid.getTickManager().wakeDevice(node));
            }
        } else {
            if (oldState && this.states <= 0) {
                this.getMainNode().ifPresent((grid, node) -> grid.getTickManager().sleepDevice(node));
            }
        }
    }

    public int usedThread() {
        // Too many outputs are jammed, stop accepting new crafting jobs
        if (this.outputBuffer.size > MAX_BUFF) {
            return MAX_THREAD;
        }
        int cnt = 0;
        for (var t : this.threads) {
            if (t.isAwake()) {
                cnt ++;
            }
        }
        return cnt;
    }

    public boolean pushJob(IPatternDetails patternDetails, KeyCounter[] inputHolder) {
        if (this.outputBuffer.size > MAX_BUFF) {
            return false;
        }
        for (var thread : this.threads) {
            if (thread.acceptJob(patternDetails, inputHolder, Direction.DOWN)) {
                this.cluster.updateCrafter(this);
                return true;
            }
        }
        return false;
    }

    public void stop() {
        for (var thread : this.threads) {
            thread.stop();
        }
        if (this.outputBuffer.size != 0) {
            var node = this.getMainNode().getNode();
            if (node != null) {
                this.outputBuffer.flush((what, amount) -> node.getGrid().getService(IStorageService.class).getInventory().insert(what, amount, Actionable.MODULATE, this.getSrc()));
            }
        }
        this.saveChanges();
    }

    public void addToBuffer(AEKey what, long amount) {
        if (what != null && amount > 0) {
            this.outputBuffer.add(what, amount);
        }
    }

    @Override
    public void saveAdditional(CompoundTag data, HolderLookup.Provider registries) {
        super.saveAdditional(data, registries);
        for (int x = 0; x < MAX_THREAD; x ++) {
            var tag = this.threads[x].writeNBT(registries);
            data.put("#ct" + x, tag);
        }
        final CompoundTag opt = new CompoundTag();
        for (int x = 0; x < this.internalInv.size(); x++) {
            var is = this.internalInv.getStackInSlot(x);
            opt.put("item" + x, is.saveOptional(registries));
        }
        data.put("inv", opt);
        this.outputBuffer.save(data, registries, "buffer");
    }

    @Override
    public void loadTag(CompoundTag data, HolderLookup.Provider registries) {
        super.loadTag(data, registries);
        for (int x = 0; x < MAX_THREAD; x ++) {
            if (data.contains("#ct" + x)) {
                this.threads[x].readNBT(data.getCompound("#ct" + x), registries);
            }
        }
        var opt = data.getCompound("inv");
        for (int x = 0; x < this.internalInv.size(); x++) {
            var item = opt.getCompound("item" + x);
            this.internalInv.setItemDirect(x, ItemStack.parseOptional(registries, item));
        }
        this.outputBuffer.load(data, registries, "buffer");
    }

    @Override
    public void add(ClusterAssemblerMatrix c) {
        c.addCrafter(this);
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        var isAwake = false;
        for (var t : this.threads) {
            t.recalculatePlan();
            t.updateSleepiness();
            isAwake |= t.isAwake();
        }
        if (this.outputBuffer.size != 0) {
            isAwake = true;
        }
        return new TickingRequest(1, 1, !isAwake);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        if (this.blockCoolDown > 0) {
            this.blockCoolDown -= ticksSinceLastCall;
        }
        if (this.cluster == null) {
            return TickRateModulation.SLEEP;
        }
        var rate = TickRateModulation.SLEEP;
        for (var t : this.threads) {
            if (t.isAwake()) {
                var tr = t.tick(this.cluster.getSpeedCore(), ticksSinceLastCall);
                if (tr.ordinal() > rate.ordinal()) {
                    rate = tr;
                }
            }
        }
        if (this.outputBuffer.size != 0) {
            if (blockCoolDown <= 0) {
                this.outputBuffer.flush((what, amount) -> node.getGrid().getService(IStorageService.class).getInventory().insert(what, amount, Actionable.MODULATE, this.getSrc()));
                if (this.outputBuffer.size != 0) {
                    // be clammed
                    this.blockCoolDown = COOL_TIME;
                }
                rate = TickRateModulation.FASTER;
                this.saveChanges();
            } else {
                rate = TickRateModulation.SAME;
            }
        }
        this.cluster.updateCrafter(this);
        return rate;
    }

    @Override
    public void saveChangedInventory(AppEngInternalInventory inv) {
        for (var t : this.threads) {
            if (inv == t.getInternalInventory()) {
                t.recalculatePlan();
                break;
            }
        }
        this.saveChanges();
    }

    @Override
    public void onChangeInventory(AppEngInternalInventory inv, int slot) {
        this.saveChangedInventory(inv);
    }

    @Override
    public void addAdditionalDrops(Level level, BlockPos pos, List<ItemStack> drops) {
        super.addAdditionalDrops(level, pos, drops);
        for (var stack : this.internalInv) {
            var genericStack = GenericStack.unwrapItemStack(stack);
            if (genericStack != null) {
                genericStack.what().addDrops(genericStack.amount(), drops, level, pos);
            } else {
                drops.add(stack);
            }
        }
        for (var entry : this.outputBuffer.buffer.object2LongEntrySet()) {
            if (entry.getLongValue() > 0) {
                entry.getKey().addDrops(entry.getLongValue(), drops, level, pos);
            }
        }
    }

    @Override
    public void clearContent() {
        super.clearContent();
        this.internalInv.clear();
        this.outputBuffer.clear();
    }

    private static class OutputBuffer {

        private final Object2LongOpenHashMap<AEKey> buffer;
        private long size = 0;

        private OutputBuffer() {
            this.buffer = new Object2LongOpenHashMap<>();
        }

        public void clear() {
            this.buffer.clear();
            this.size = 0;
        }

        public void add(AEKey key, long amount) {
            var stored = this.buffer.getLong(key);
            this.buffer.put(key, stored + amount);
            this.size += amount;
        }

        public void flush(BiFunction<AEKey, Long, Long> pusher) {
            this.size = 0;
            List<GenericStack> result = new ArrayList<>(this.buffer.size());
            for (var entry : this.buffer.object2LongEntrySet()) {
                var key = entry.getKey();
                var amount = entry.getLongValue();
                var stored = pusher.apply(key, amount);
                if (stored < amount) {
                    result.add(new GenericStack(key, amount - stored));
                    this.size += (amount - stored);
                }
            }
            this.buffer.clear();
            if (!result.isEmpty()) {
                for (var stack : result) {
                    this.buffer.put(stack.what(), stack.amount());
                }
            }
        }

        public void save(CompoundTag tag, HolderLookup.Provider registries, String name) {
            var tagList = new ListTag();
            for (var entry : buffer.object2LongEntrySet()) {
                var key = entry.getKey();
                var value = entry.getLongValue();
                if (key != null && value > 0) {
                    tagList.add(GenericStack.writeTag(registries, new GenericStack(key, value)));
                }
            }
            tag.put(name, tagList);
        }

        public void load(CompoundTag tag, HolderLookup.Provider registries, String name) {
            var tagList = tag.getList(name, Tag.TAG_COMPOUND);
            for (var e : tagList) {
                var stack = GenericStack.readTag(registries, (CompoundTag) e);
                if (stack != null && stack.amount() > 0) {
                    this.buffer.put(stack.what(), stack.amount());
                }
            }
        }

    }

}
