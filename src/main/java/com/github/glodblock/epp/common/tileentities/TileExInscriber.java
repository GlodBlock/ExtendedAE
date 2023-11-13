package com.github.glodblock.epp.common.tileentities;

import appeng.api.config.Setting;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.implementations.blockentities.ICrankable;
import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.orientation.BlockOrientation;
import appeng.api.orientation.RelativeSide;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import appeng.api.util.AECableType;
import appeng.api.util.IConfigManager;
import appeng.api.util.IConfigurableObject;
import appeng.blockentity.grid.AENetworkPowerBlockEntity;
import appeng.capabilities.Capabilities;
import appeng.core.settings.TickRates;
import appeng.recipes.handlers.InscriberRecipe;
import appeng.util.ConfigManager;
import appeng.util.inv.CombinedInternalInventory;
import com.github.glodblock.epp.common.EPPItemAndBlock;
import com.github.glodblock.epp.common.me.Crankable;
import com.github.glodblock.epp.common.me.InscriberThread;
import com.github.glodblock.epp.util.FCUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class TileExInscriber extends AENetworkPowerBlockEntity implements IGridTickable, IUpgradeableObject, IConfigurableObject {

    public static final int MAX_THREAD = 4;
    private final InscriberThread[] threads = new InscriberThread[MAX_THREAD];
    private final IUpgradeInventory upgrades;
    private final ConfigManager configManager;
    private final InternalInventory topItemHandlerAll;
    private final InternalInventory bottomItemHandlerAll;
    private final InternalInventory sideItemHandlerAll;
    private final InternalInventory combinedItemHandlerExternAll;
    private final InternalInventory invAll;
    private int stackSize = 1;
    private int animationIndex = -1;

    public TileExInscriber(BlockPos pos, BlockState blockState) {
        super(FCUtil.getTileType(TileExInscriber.class, TileExInscriber::new, EPPItemAndBlock.EX_INSCRIBER), pos, blockState);
        this.getMainNode()
                .setIdlePowerUsage(0)
                .addService(IGridTickable.class, this);
        this.setInternalMaxPower(1600);
        this.upgrades = UpgradeInventories.forMachine(EPPItemAndBlock.EX_INSCRIBER, 4, this::saveChanges);
        this.configManager = new ConfigManager(this::onConfigChanged);
        this.configManager.registerSetting(Settings.INSCRIBER_SEPARATE_SIDES, YesNo.NO);
        this.configManager.registerSetting(Settings.AUTO_EXPORT, YesNo.NO);
        var invs = new InternalInventory[MAX_THREAD];
        var invs2 = new InternalInventory[MAX_THREAD];
        var invs3 = new InternalInventory[MAX_THREAD];
        var invs4 = new InternalInventory[MAX_THREAD];
        var invs5 = new InternalInventory[MAX_THREAD];
        for (int x = 0; x < MAX_THREAD; x ++) {
            this.threads[x] = new InscriberThread(this);
            invs[x] = this.threads[x].topItemHandlerExtern;
            invs2[x] = this.threads[x].bottomItemHandlerExtern;
            invs3[x] = this.threads[x].sideItemHandlerExtern;
            invs4[x] = this.threads[x].combinedItemHandlerExtern;
            invs5[x] = this.threads[x].getInternalInventory();
        }
        this.topItemHandlerAll = new CombinedInternalInventory(invs);
        this.bottomItemHandlerAll = new CombinedInternalInventory(invs2);
        this.sideItemHandlerAll = new CombinedInternalInventory(invs3);
        this.combinedItemHandlerExternAll = new CombinedInternalInventory(invs4);
        this.invAll = new CombinedInternalInventory(invs5);
        this.setPowerSides(getGridConnectableSides(getOrientation()));
    }

    public int getMaxProcessingTime() {
        return this.threads[0].getMaxProcessingTime();
    }

    public int getProcessingTime(int index) {
        return this.threads[index].getProcessingTime();
    }

    public boolean isSmash() {
        if (this.animationIndex < 0) {
            for (int x = 0; x < MAX_THREAD; x ++) {
                if (this.threads[x].isSmash() && this.checkTime(this.threads[x].getClientStart())) {
                    this.animationIndex = x;
                    return true;
                }
            }
        } else {
            return this.threads[this.animationIndex].isSmash();
        }
        return false;
    }

    // Prevent wrong synced animation
    private boolean checkTime(long clientTime) {
        final long currentTime = System.currentTimeMillis();
        if (clientTime == 0) {
            return true;
        }
        return currentTime - clientTime < 100;
    }

    public long getClientStart() {
        if (this.animationIndex < 0) {
            return 0;
        }
        return this.threads[this.animationIndex].getClientStart();
    }

    public void setSmash(boolean smash) {
        if (this.animationIndex < 0) {
            return;
        }
        this.threads[this.animationIndex].setSmash(smash);
        if (!smash) {
            this.animationIndex = -1;
        }
    }

    public InternalInventory getIndexInventory(int index) {
        return this.threads[index].getInternalInventory();
    }

    public InscriberRecipe getTask(int index) {
        return this.threads[index].getTask();
    }

    private void onConfigChanged(IConfigManager manager, Setting<?> setting) {
        if (setting == Settings.AUTO_EXPORT) {
            getMainNode().ifPresent((grid, node) -> grid.getTickManager().wakeDevice(node));
        }
        if (setting == Settings.INSCRIBER_SEPARATE_SIDES) {
            markForUpdate();
        }
        saveChanges();
    }

    @Override
    protected InternalInventory getExposedInventoryForSide(Direction facing) {
        if (isSeparateSides()) {
            if (facing == this.getTop()) {
                return this.topItemHandlerAll;
            } else if (facing == this.getTop().getOpposite()) {
                return this.bottomItemHandlerAll;
            } else {
                return this.sideItemHandlerAll;
            }
        } else {
            return this.combinedItemHandlerExternAll;
        }
    }

    @Override
    public AECableType getCableConnectionType(Direction dir) {
        return AECableType.COVERED;
    }

    @Override
    public void saveAdditional(CompoundTag data) {
        super.saveAdditional(data);
        this.upgrades.writeToNBT(data, "upgrades");
        this.configManager.writeToNBT(data);
        data.putInt("stacksize", this.stackSize);
    }

    @Override
    public void loadTag(CompoundTag data) {
        super.loadTag(data);
        this.upgrades.readFromNBT(data, "upgrades");
        this.configManager.readFromNBT(data);
        this.stackSize = data.contains("stacksize") ? data.getInt("stacksize") : 1;
        for (var t : this.threads) {
            t.init();
            t.setStackSize(this.stackSize);
        }
    }

    public int getInvStackSize() {
        return this.stackSize;
    }

    public void setInvStackSize(int size) {
        this.stackSize = size;
        for (var t : this.threads) {
            t.setStackSize(size);
        }
        this.markForUpdate();
        this.saveChanges();
    }

    @Override
    protected boolean readFromStream(FriendlyByteBuf data) {
        boolean c = super.readFromStream(data);
        for (var t : this.threads) {
            t.readFromStream(data);
        }
        this.stackSize = data.readVarInt();
        return c;
    }

    @Override
    protected void writeToStream(FriendlyByteBuf data) {
        super.writeToStream(data);
        for (var t : this.threads) {
            t.writeToStream(data);
        }
        data.writeVarInt(this.stackSize);
    }

    @Override
    protected void saveVisualState(CompoundTag data) {
        super.saveVisualState(data);
        for (int x = 0; x < MAX_THREAD; x ++) {
            data.putBoolean("smash#" + x, this.threads[x].isSmash());
        }
    }

    @Override
    protected void loadVisualState(CompoundTag data) {
        super.loadVisualState(data);
        for (int x = 0; x < MAX_THREAD; x ++) {
            this.threads[x].setSmash(data.getBoolean("smash#" + x));
        }
    }

    @Override
    public Set<Direction> getGridConnectableSides(BlockOrientation orientation) {
        return EnumSet.complementOf(EnumSet.of(orientation.getSide(RelativeSide.FRONT)));
    }

    @Override
    protected void onOrientationChanged(BlockOrientation orientation) {
        super.onOrientationChanged(orientation);
        this.setPowerSides(getGridConnectableSides(orientation));
    }

    @Override
    public void addAdditionalDrops(Level level, BlockPos pos, List<ItemStack> drops) {
        super.addAdditionalDrops(level, pos, drops);
        for (var upgrade : upgrades) {
            drops.add(upgrade);
        }
    }

    @Override
    public void clearContent() {
        super.clearContent();
        upgrades.clear();
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        var rate = TickRateModulation.SLEEP;
        for (var t : this.threads) {
            var tr = t.tick();
            if (tr.ordinal() > rate.ordinal()) {
                rate = tr;
            }
        }
        return rate;
    }

    @Nullable
    @Override
    public InternalInventory getSubInventory(ResourceLocation id) {
        if (id.equals(ISegmentedInventory.STORAGE)) {
            return this.getInternalInventory();
        } else if (id.equals(ISegmentedInventory.UPGRADES)) {
            return this.upgrades;
        }
        return super.getSubInventory(id);
    }

    public boolean isSeparateSides() {
        return this.configManager.getSetting(Settings.INSCRIBER_SEPARATE_SIDES) == YesNo.YES;
    }

    @Override
    public IUpgradeInventory getUpgrades() {
        return upgrades;
    }

    @Override
    public ConfigManager getConfigManager() {
        return configManager;
    }

    @Override
    public InternalInventory getInternalInventory() {
        return this.invAll;
    }

    @Override
    public void onChangeInventory(InternalInventory inv, int slot) {
        this.markForUpdate();
        this.saveChanges();
        for (var t : this.threads) {
            if (t.containsInv(inv)) {
                t.onChangeInventory(inv, slot);
                break;
            }
        }
        getMainNode().ifPresent((grid, node) -> grid.getTickManager().wakeDevice(node));
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        var sleep = true;
        for (var t : this.threads) {
            if (!t.isSleep()) {
                sleep = false;
                break;
            }
        }
        return new TickingRequest(TickRates.Inscriber, sleep, false);
    }

    public ICrankable getCrankable(Direction direction) {
        if (direction != getFront()) {
            return new Crankable(this);
        }
        return null;
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
        if (Capabilities.CRANKABLE.equals(capability)) {
            var crankable = getCrankable(facing);
            if (crankable == null) {
                return LazyOptional.empty();
            }
            return Capabilities.CRANKABLE.orEmpty(
                    capability,
                    LazyOptional.of(() -> crankable));
        }
        return super.getCapability(capability, facing);
    }

}
