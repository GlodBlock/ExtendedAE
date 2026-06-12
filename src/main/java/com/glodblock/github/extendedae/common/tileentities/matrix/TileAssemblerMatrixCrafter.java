package com.glodblock.github.extendedae.common.tileentities.matrix;

import appeng.api.crafting.IPatternDetails;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.CombinedInternalInventory;
import appeng.util.inv.InternalInventoryHost;
import com.glodblock.github.extendedae.common.me.CraftingMatrixThread;
import com.glodblock.github.extendedae.common.me.CraftingThread;
import com.glodblock.github.extendedae.common.me.matrix.ClusterAssemblerMatrix;
import com.glodblock.github.extendedae.util.SingleThreadLRU;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.List;

public class TileAssemblerMatrixCrafter extends TileAssemblerMatrixFunction implements InternalInventoryHost, IGridTickable {

    public static final int MAX_THREAD = 8;
    private final CraftingThread[] threads = new CraftingThread[MAX_THREAD];
    private final InternalInventory internalInv;
    private short states = 0b000000;

    public TileAssemblerMatrixCrafter(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        this.getMainNode().addService(IGridTickable.class, this);
        SingleThreadLRU<AEItemKey, InternalInventory> cache = new SingleThreadLRU<>(new Object2ObjectOpenHashMap<>(20));
        var invs = new InternalInventory[MAX_THREAD];
        for (int x = 0; x < MAX_THREAD; x ++) {
            final int index = x;
            this.threads[index] = new CraftingMatrixThread(this, this::getSrc, signal -> this.changeState(index, signal), cache);
            invs[index] = this.threads[index].getInternalInventory();
        }
        this.internalInv = new CombinedInternalInventory(invs);
    }

    private IActionSource getSrc() {
        return this.cluster.getSrc();
    }

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
        int cnt = 0;
        for (var t : this.threads) {
            if (t.isAwake()) {
                cnt ++;
            }
        }
        return cnt;
    }

    public boolean pushJob(IPatternDetails patternDetails, KeyCounter[] inputHolder) {
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
    }

    @Override
    public void saveAdditional(ValueOutput data) {
        super.saveAdditional(data);
        for (int x = 0; x < MAX_THREAD; x ++) {
            this.threads[x].writeNBT(data.child("#ct" + x));
        }
        final ValueOutput opt = data.child("inv");
        for (int x = 0; x < this.internalInv.size(); x++) {
            var is = this.internalInv.getStackInSlot(x);
            opt.store("item" + x, ItemStack.OPTIONAL_CODEC, is);
        }
    }

    @Override
    public void loadTag(ValueInput data) {
        super.loadTag(data);
        for (int x = 0; x < MAX_THREAD; x ++) {
            data.child("#ct" + x).ifPresent(this.threads[x]::readNBT);
        }
        data.child("inv").ifPresent(input -> {
            for (int x = 0; x < this.internalInv.size(); x++) {
                var item = input.read("item" + x, ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
                this.internalInv.setItemDirect(x, item);
            }
        });
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
        return new TickingRequest(1, 1, !isAwake);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
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
        for (var stack: this.internalInv) {
            var genericStack = GenericStack.unwrapItemStack(stack);
            if (genericStack != null) {
                genericStack.what().addDrops(genericStack.amount(), drops, level, pos);
            } else {
                drops.add(stack);
            }
        }
    }

    @Override
    public void clearContent() {
        super.clearContent();
        this.internalInv.clear();
    }

}
