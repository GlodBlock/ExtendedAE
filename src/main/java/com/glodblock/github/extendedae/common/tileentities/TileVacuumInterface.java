package com.glodblock.github.extendedae.common.tileentities;

import appeng.api.config.Actionable;
import appeng.api.config.FuzzyMode;
import appeng.api.config.IncludeExclude;
import appeng.api.config.RedstoneMode;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import appeng.api.util.IConfigManager;
import appeng.api.util.IConfigurableObject;
import appeng.blockentity.grid.AENetworkedBlockEntity;
import appeng.core.definitions.AEItems;
import appeng.helpers.IConfigInvHost;
import appeng.helpers.externalstorage.GenericStackInv;
import appeng.util.ConfigInventory;
import appeng.util.ConfigManager;
import appeng.util.prioritylist.IPartitionList;
import com.glodblock.github.extendedae.client.render.StageTESRTile;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.glodium.util.GlodUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.List;

public class TileVacuumInterface extends AENetworkedBlockEntity implements IConfigInvHost, IConfigurableObject, IUpgradeableObject, IGridTickable, StageTESRTile {

    public static final int MAX_SIZE = 10;
    public static final int MAX_OFFSET = 16;
    private final ConfigInventory config;
    private final IUpgradeInventory upgrades;
    private final ConfigManager configManager;
    private final IActionSource source;
    private IncludeExclude listMode = IncludeExclude.WHITELIST;
    private IPartitionList filter;
    private YesNo lastRedstoneState;
    private YesNo isWorking = YesNo.UNDECIDED;
    private BlockPos size = new BlockPos(5, 5, 5);
    private BlockPos offset = BlockPos.ZERO;
    private AABB workArea;
    private boolean displayArea;

    public TileVacuumInterface(BlockPos pos, BlockState blockState) {
        super(GlodUtil.getTileType(TileVacuumInterface.class, TileVacuumInterface::new, EAESingletons.VACUUM_INTERFACE), pos, blockState);
        this.getMainNode().setFlags().setIdlePowerUsage(1).addService(IGridTickable.class, this);
        this.config = ConfigInventory.configTypes(63).supportedType(AEKeyType.items()).changeListener(this::onChanged).build();
        this.upgrades = UpgradeInventories.forMachine(EAESingletons.VACUUM_INTERFACE, 5, this::onChanged);
        this.source = IActionSource.ofMachine(this);
        this.lastRedstoneState = YesNo.UNDECIDED;
        this.configManager = new ConfigManager(this::onChanged);
        this.configManager.registerSetting(Settings.FUZZY_MODE, FuzzyMode.IGNORE_ALL);
        this.configManager.registerSetting(Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE);
    }

    public BlockPos getSize() {
        return this.size;
    }

    public BlockPos getOffset() {
        return this.offset;
    }

    public void setSize(BlockPos size) {
        this.size = new BlockPos(
                (int) GlodUtil.clamp(size.getX(), 1, MAX_SIZE),
                (int) GlodUtil.clamp(size.getY(), 1, MAX_SIZE),
                (int) GlodUtil.clamp(size.getZ(), 1, MAX_SIZE)
        );
        this.workArea = this.createWorkArea();
        this.fastUpdate();
        this.setChanged();
    }

    public void setOffset(BlockPos offset) {
        this.offset = new BlockPos(
                (int) GlodUtil.clamp(offset.getX(), -MAX_OFFSET, MAX_OFFSET),
                (int) GlodUtil.clamp(offset.getY(), -MAX_OFFSET, MAX_OFFSET),
                (int) GlodUtil.clamp(offset.getZ(), -MAX_OFFSET, MAX_OFFSET)
        );
        this.workArea = this.createWorkArea();
        this.fastUpdate();
        this.setChanged();
    }

    public void setDisplayArea(boolean enable) {
        var oldValue = this.displayArea;
        this.displayArea = enable;
        if (oldValue != this.displayArea) {
            this.fastUpdate();
        }
    }

    public boolean isDisplayArea() {
        return this.displayArea;
    }

    public IPartitionList getFilter() {
        if (this.filter == null) {
            this.filter = this.createFilter();
        }
        return this.filter;
    }

    public AABB getWorkArea() {
        if (this.workArea == null) {
            this.workArea = this.createWorkArea();
        }
        return this.workArea;
    }

    private void onChanged() {
        this.filter = this.createFilter();
        this.changeWorkStatus();
        this.updateSleepness();
        this.saveChanges();
    }

    private void fastUpdate() {
        if (this.level != null && !this.isRemoved() && !notLoaded()) {
            BlockState currentState = getBlockState();
            this.level.sendBlockUpdated(this.worldPosition, currentState, currentState, Block.UPDATE_NEIGHBORS);
        }
    }

    private IPartitionList createFilter() {
        this.listMode = this.isUpgradedWith(AEItems.INVERTER_CARD) ? IncludeExclude.BLACKLIST : IncludeExclude.WHITELIST;
        var builder = IPartitionList.builder();
        if (this.isUpgradedWith(AEItems.FUZZY_CARD)) {
            builder.fuzzyMode(this.getConfigManager().getSetting(Settings.FUZZY_MODE));
        }
        var slotsToUse = 18 + this.getInstalledUpgrades(AEItems.CAPACITY_CARD) * 9;
        for (var x = 0; x < this.config.size() && x < slotsToUse; x++) {
            builder.add(this.config.getKey(x));
        }
        return builder.build();
    }

    private AABB createWorkArea() {
        var center = castPos(this.getBlockPos().offset(this.offset));
        var halfX = this.size.getX() / 2;
        var halfY = this.size.getY() / 2;
        var halfZ = this.size.getZ() / 2;
        var max = center.add(-halfX, -halfY, -halfZ);
        var min = center.add(this.size.getX() - halfX, this.size.getY() - halfY, this.size.getZ() - halfZ);
        return new AABB(
                min.x(), min.y(), min.z(), max.x(), max.y(), max.z()
        );
    }

    private static Vec3 castPos(BlockPos pos) {
        return new Vec3(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public void saveAdditional(CompoundTag data, HolderLookup.Provider registries) {
        super.saveAdditional(data, registries);
        this.config.writeToChildTag(data, "config", registries);
        this.upgrades.writeToNBT(data, "upgrades", registries);
        data.putLong("size", this.size.asLong());
        data.putLong("offset", this.offset.asLong());
        data.putInt("lastRedstoneState", this.lastRedstoneState.ordinal());
        this.configManager.writeToNBT(data, registries);
        data.putBoolean("displayArea", this.displayArea);
    }

    @Override
    public void loadTag(CompoundTag data, HolderLookup.Provider registries) {
        super.loadTag(data,registries);
        this.config.readFromChildTag(data, "config", registries);
        this.upgrades.readFromNBT(data, "upgrades", registries);
        if (data.contains("size")) {
            this.size = BlockPos.of(data.getLong("size"));
        }
        if (data.contains("lastRedstoneState")) {
            this.lastRedstoneState = YesNo.values()[data.getInt("lastRedstoneState")];
        }
        this.offset = BlockPos.of(data.getLong("offset"));
        this.configManager.readFromNBT(data, registries);
        this.displayArea = data.getBoolean("displayArea");
    }

    @Override
    public IUpgradeInventory getUpgrades() {
        return this.upgrades;
    }

    @Override
    public GenericStackInv getConfig() {
        return this.config;
    }

    @Override
    public IConfigManager getConfigManager() {
        return this.configManager;
    }

    @Override
    protected boolean readFromStream(RegistryFriendlyByteBuf data) {
        var changed = super.readFromStream(data);
        var displayArea = data.readBoolean();
        if (this.displayArea != displayArea) {
            this.displayArea = displayArea;
            changed = true;
        }
        var size = data.readBlockPos();
        if (!this.size.equals(size)) {
            this.size = size;
            changed = true;
        }
        var offset = data.readBlockPos();
        if (!this.offset.equals(offset)) {
            this.offset = offset;
            changed = true;
        }
        if (changed) {
            this.workArea = this.createWorkArea();
        }
        return changed;
    }

    @Override
    protected void writeToStream(RegistryFriendlyByteBuf data) {
        super.writeToStream(data);
        data.writeBoolean(this.displayArea);
        data.writeBlockPos(this.size);
        data.writeBlockPos(this.offset);
    }

    public void changeWorkStatus() {
        this.isWorking = YesNo.UNDECIDED;
    }

    public void updateSleepness() {
        if (this.isWorking()) {
            this.getMainNode().ifPresent((grid, node) -> grid.getTickManager().alertDevice(node));
        } else {
            this.getMainNode().ifPresent((grid, node) -> grid.getTickManager().sleepDevice(node));
        }
    }

    public void updateRedstoneState() {
        YesNo currentState = YesNo.UNDECIDED;
        if (this.level != null) {
            currentState = this.level.getBestNeighborSignal(this.worldPosition) != 0 ? YesNo.YES : YesNo.NO;
        }
        if (this.lastRedstoneState != currentState) {
            this.lastRedstoneState = currentState;
        }
    }

    private boolean getRedstoneState() {
        if (this.lastRedstoneState == YesNo.UNDECIDED) {
            this.updateRedstoneState();
        }
        return this.lastRedstoneState == YesNo.YES;
    }

    private boolean checkEnable() {
        if (!this.upgrades.isInstalled(AEItems.REDSTONE_CARD)) {
            return true;
        }
        final RedstoneMode rs = this.configManager.getSetting(Settings.REDSTONE_CONTROLLED);
        if (rs == RedstoneMode.HIGH_SIGNAL) {
            return this.getRedstoneState();
        }
        return !this.getRedstoneState();
    }

    private boolean isWorking() {
        if (this.isWorking == YesNo.UNDECIDED) {
            this.isWorking = this.checkEnable() ? YesNo.YES : YesNo.NO;
        }
        return this.isWorking == YesNo.YES;
    }

    public TickRateModulation doWork() {
        if (this.isWorking() && this.level != null) {
            var items = this.level.getEntitiesOfClass(ItemEntity.class, this.getWorkArea());
            if (items.isEmpty()) {
                return TickRateModulation.SLOWER;
            }
            this.getMainNode().ifPresent(gird -> {
                var storage = gird.getStorageService();
                for (var entity : items) {
                    if (entity.isAlive()) {
                        var key = AEItemKey.of(entity.getItem());
                        int amount = entity.getItem().getCount();
                        if (this.getFilter().matchesFilter(key, this.listMode)) {
                            var added = storage.getInventory().insert(key, amount, Actionable.MODULATE, this.source);
                            if (added < amount) {
                                entity.getItem().shrink((int) added);
                            } else {
                                entity.discard();
                            }
                        }
                    }
                }
            });
            return TickRateModulation.URGENT;
        }
        return TickRateModulation.SLEEP;
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        return new TickingRequest(1, 10, !this.isWorking());
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        return this.doWork();
    }

    @Override
    public void addAdditionalDrops(Level level, BlockPos pos, List<ItemStack> drops) {
        super.addAdditionalDrops(level, pos, drops);
        for (var upgrade : this.upgrades) {
            drops.add(upgrade);
        }
    }

    @Override
    public void clearContent() {
        super.clearContent();
        this.upgrades.clear();
    }

    @Override
    public RenderLevelStageEvent.Stage renderStage() {
        return RenderLevelStageEvent.Stage.AFTER_PARTICLES;
    }

}
