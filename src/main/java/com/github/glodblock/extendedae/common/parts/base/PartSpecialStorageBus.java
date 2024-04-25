package com.github.glodblock.extendedae.common.parts.base;

import appeng.api.behaviors.ExternalStorageStrategy;
import appeng.api.config.AccessRestriction;
import appeng.api.config.IncludeExclude;
import appeng.api.config.Setting;
import appeng.api.config.Settings;
import appeng.api.config.StorageFilter;
import appeng.api.config.YesNo;
import appeng.api.features.IPlayerRegistry;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridNodeListener;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartHost;
import appeng.api.parts.IPartItem;
import appeng.api.stacks.AEKeyType;
import appeng.api.storage.IStorageMounts;
import appeng.api.storage.IStorageProvider;
import appeng.api.storage.MEStorage;
import appeng.api.util.AECableType;
import appeng.api.util.IConfigManager;
import appeng.core.definitions.AEItems;
import appeng.core.settings.TickRates;
import appeng.core.stats.AdvancementTriggers;
import appeng.helpers.IPriorityHost;
import appeng.helpers.InterfaceLogicHost;
import appeng.me.helpers.MachineSource;
import appeng.me.storage.CompositeStorage;
import appeng.me.storage.ITickingMonitor;
import appeng.me.storage.MEInventoryHandler;
import appeng.me.storage.NullInventory;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import appeng.parts.PartAdjacentApi;
import appeng.parts.automation.StackWorldBehaviors;
import appeng.parts.automation.UpgradeablePart;
import appeng.util.Platform;
import appeng.util.prioritylist.IPartitionList;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public abstract class PartSpecialStorageBus extends UpgradeablePart implements IGridTickable, IStorageProvider, IPriorityHost {

    protected final IActionSource source;
    protected final StorageBusInventory handler = this.createHandler();
    @Nullable
    protected Component handlerDescription;
    protected final PartAdjacentApi<MEStorage> adjacentStorageAccessor;
    @Nullable
    protected Map<AEKeyType, ExternalStorageStrategy> externalStorageStrategies;
    protected boolean wasOnline = false;
    protected int priority = 0;

    protected PendingUpdateStatus updateStatus = PendingUpdateStatus.FAST_UPDATE;
    protected ITickingMonitor monitor = null;
    protected IPartitionList filter = null;

    public PartSpecialStorageBus(IPartItem<?> partItem) {
        super(partItem);
        this.adjacentStorageAccessor = new PartAdjacentApi<>(this, MEStorage.SIDED);
        this.getConfigManager().registerSetting(Settings.ACCESS, AccessRestriction.READ_WRITE);
        this.getConfigManager().registerSetting(Settings.STORAGE_FILTER, StorageFilter.EXTRACTABLE_ONLY);
        this.getConfigManager().registerSetting(Settings.FILTER_ON_EXTRACT, YesNo.YES);
        this.source = new MachineSource(this);
        getMainNode().addService(IStorageProvider.class, this).addService(IGridTickable.class, this);
    }

    @Override
    protected final void onMainNodeStateChanged(IGridNodeListener.State reason) {
        var currentOnline = this.getMainNode().isOnline();
        if (this.wasOnline != currentOnline) {
            this.wasOnline = currentOnline;
            this.getHost().markForUpdate();
            remountStorage();
        }
    }

    private void remountStorage() {
        IStorageProvider.requestUpdate(getMainNode());
    }

    @Override
    public void onSettingChanged(IConfigManager manager, Setting<?> setting) {
        this.forceUpdate();
        this.getHost().markForSave();
    }

    @Override
    public void upgradesChanged() {
        super.upgradesChanged();
        this.forceUpdate();
    }

    protected StorageBusInventory createHandler() {
        return new StorageBusInventory(NullInventory.of());
    }

    @Override
    public void readFromNBT(CompoundTag data) {
        super.readFromNBT(data);
        this.priority = data.getInt("priority");
    }

    @Override
    public void writeToNBT(CompoundTag data) {
        super.writeToNBT(data);
        data.putInt("priority", this.priority);
    }

    @Override
    public final boolean onPartActivate(Player player, InteractionHand hand, Vec3 pos) {
        if (!isClientSide()) {
            openConfigMenu(player);
        }
        return true;
    }

    protected final void openConfigMenu(Player player) {
        MenuOpener.open(getMenuType(), player, MenuLocators.forPart(this));
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu subMenu) {
        MenuOpener.returnTo(getMenuType(), player, MenuLocators.forPart(this));
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return new ItemStack(getPartItem());
    }

    @Override
    public void getBoxes(IPartCollisionHelper bch) {
        bch.addBox(3, 3, 15, 13, 13, 16);
        bch.addBox(2, 2, 14, 14, 14, 15);
        bch.addBox(5, 5, 12, 11, 11, 14);
    }

    @Override
    public float getCableConnectionLength(AECableType cable) {
        return 4;
    }

    @Override
    public void onNeighborChanged(BlockGetter level, BlockPos pos, BlockPos neighbor) {
        if (pos.relative(getSide()).equals(neighbor)) {
            var te = level.getBlockEntity(neighbor);

            if (te == null) {
                // In case the TE was destroyed, we have to update the target handler immediately.
                this.updateTarget(false);
            } else {
                this.scheduleUpdate();
            }
        }
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        return new TickingRequest(TickRates.StorageBus, false, true);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        if (this.updateStatus != PendingUpdateStatus.NO_UPDATE) {
            this.updateTarget(false);
        }

        if (this.monitor != null) {
            return this.monitor.onTick();
        }

        return this.updateStatus == PendingUpdateStatus.SLOW_UPDATE ? TickRateModulation.IDLE : TickRateModulation.SLEEP;
    }

    private boolean hasRegisteredCellToNetwork() {
        return getMainNode().isOnline() && !(this.handler.getDelegate() instanceof NullInventory);
    }

    public Component getConnectedToDescription() {
        return handlerDescription;
    }

    protected void forceUpdate() {
        if (getMainNode().isReady()) {
            this.updateTarget(true);
        }
    }

    protected void updateTarget(boolean forceFullUpdate) {
        if (isClientSide()) {
            return; // Part is not part of level yet or its client-side
        }

        MEStorage foundMonitor = null;
        Map<AEKeyType, MEStorage> foundExternalApi = Collections.emptyMap();

        // If the target position is not ticking, don't search for a target.
        if (Platform.areBlockEntitiesTicking(getLevel(), getBlockEntity().getBlockPos().relative(getSide()))) {
            // In any case we don't need any further update
            this.updateStatus = PendingUpdateStatus.NO_UPDATE;

            // Prioritize a handler to directly link to another ME network
            foundMonitor = adjacentStorageAccessor.find();

            if (foundMonitor == null) {
                // Query all available external APIs
                // TODO: If a filter is configured, we might want to only query external APIs for compatible key spaces
                foundExternalApi = new IdentityHashMap<>(2);
                findExternalStorages(foundExternalApi);
            }
        } else {
            // Try again in the future...
            this.updateStatus = PendingUpdateStatus.SLOW_UPDATE;
        }

        if (!forceFullUpdate && this.handler.getDelegate() instanceof CompositeStorage compositeStorage
                && !foundExternalApi.isEmpty()) {
            // Just update the inventory reference, the ticking monitor will take care of the rest.
            compositeStorage.setStorages(foundExternalApi);
            handlerDescription = compositeStorage.getDescription();
            return;
        } else if (!forceFullUpdate && foundMonitor == this.handler.getDelegate()) {
            // Monitor didn't change, nothing to do!
            return;
        }

        var wasSleeping = this.monitor == null;
        var wasRegistered = this.hasRegisteredCellToNetwork();

        // Update inventory
        MEStorage newInventory;
        if (foundMonitor != null) {
            newInventory = foundMonitor;
            this.checkStorageBusOnInterface();
            handlerDescription = newInventory.getDescription();
        } else if (!foundExternalApi.isEmpty()) {
            newInventory = new CompositeStorage(foundExternalApi);
            handlerDescription = newInventory.getDescription();
        } else {
            newInventory = NullInventory.of();
            handlerDescription = null;
        }
        this.handler.setDelegate(newInventory);

        // Apply other settings.
        this.handler.setAccessRestriction(this.getConfigManager().getSetting(Settings.ACCESS));
        this.handler.setWhitelist(isUpgradedWith(AEItems.INVERTER_CARD) ? IncludeExclude.BLACKLIST
                : IncludeExclude.WHITELIST);

        this.handler.setPartitionList(createFilter());
        this.handler.setVoidOverflow(this.isUpgradedWith(AEItems.VOID_CARD));

        // Ensure we apply the partition list to the available items.
        boolean filterOnExtract = this.getConfigManager().getSetting(Settings.FILTER_ON_EXTRACT) == YesNo.YES;
        this.handler.setExtractFiltering(filterOnExtract, isExtractableOnly() && filterOnExtract);

        // Let the new inventory react to us ticking.
        if (newInventory instanceof ITickingMonitor tickingMonitor) {
            this.monitor = tickingMonitor;
        } else {
            this.monitor = null;
        }

        // Update sleeping state.
        if (wasSleeping != (this.monitor == null)) {
            getMainNode().ifPresent((grid, node) -> {
                var tm = grid.getTickManager();
                if (this.monitor == null) {
                    tm.sleepDevice(node);
                } else {
                    tm.wakeDevice(node);
                }
            });
        }

        if (wasRegistered != this.hasRegisteredCellToNetwork()) {
            remountStorage();
        }
    }

    protected boolean isExtractableOnly() {
        return this.getConfigManager().getSetting(Settings.STORAGE_FILTER) == StorageFilter.EXTRACTABLE_ONLY;
    }

    protected void findExternalStorages(Map<AEKeyType, MEStorage> storages) {
        var extractableOnly = isExtractableOnly();
        for (var entry : getExternalStorageStrategies().entrySet()) {
            var wrapper = entry.getValue().createWrapper(
                    extractableOnly,
                    this::invalidateOnExternalStorageChange);
            if (wrapper != null) {
                storages.put(entry.getKey(), wrapper);
            }
        }
    }

    protected void invalidateOnExternalStorageChange() {
        getMainNode().ifPresent((grid, node) -> {
            grid.getTickManager().alertDevice(node);
        });
    }

    protected void checkStorageBusOnInterface() {
        var oppositeSide = getSide().getOpposite();
        var targetPos = getBlockEntity().getBlockPos().relative(getSide());
        var targetBe = getLevel().getBlockEntity(targetPos);

        Object targetHost = targetBe;
        if (targetBe instanceof IPartHost partHost) {
            targetHost = partHost.getPart(oppositeSide);
        }

        if (targetHost instanceof InterfaceLogicHost) {
            var server = getLevel().getServer();
            var player = IPlayerRegistry.getConnected(server, this.getActionableNode().getOwningPlayerId());
            if (player != null) {
                AdvancementTriggers.RECURSIVE.trigger(player);
            }
        }
    }

    @Override
    public void mountInventories(IStorageMounts mounts) {
        if (this.hasRegisteredCellToNetwork()) {
            mounts.mount(this.handler, priority);
        }
    }

    @Override
    public final int getPriority() {
        return this.priority;
    }

    @Override
    public final void setPriority(int newValue) {
        this.priority = newValue;
        this.getHost().markForSave();
        this.remountStorage();
    }

    private Map<AEKeyType, ExternalStorageStrategy> getExternalStorageStrategies() {
        if (externalStorageStrategies == null) {
            var host = getHost().getBlockEntity();
            this.externalStorageStrategies = StackWorldBehaviors.createExternalStorageStrategies(
                    (ServerLevel) host.getLevel(),
                    host.getBlockPos().relative(getSide()),
                    getSide().getOpposite());
        }
        return externalStorageStrategies;
    }

    protected abstract IPartitionList createFilter();

    public abstract MenuType<?> getMenuType();

    protected void scheduleUpdate() {
        if (isClientSide()) {
            return;
        }
        this.updateStatus = PendingUpdateStatus.FAST_UPDATE;
        getMainNode().ifPresent((grid, node) -> grid.getTickManager().alertDevice(node));
    }

    public static class StorageBusInventory extends MEInventoryHandler {
        public StorageBusInventory(MEStorage inventory) {
            super(inventory);
        }

        @Override
        public MEStorage getDelegate() {
            return super.getDelegate();
        }

        @Override
        public void setDelegate(MEStorage delegate) {
            super.setDelegate(delegate);
        }

        public void setAccessRestriction(AccessRestriction setting) {
            setAllowExtraction(setting.isAllowExtraction());
            setAllowInsertion(setting.isAllowInsertion());
        }
    }

    public enum PendingUpdateStatus {
        FAST_UPDATE,
        SLOW_UPDATE,
        NO_UPDATE
    }

}
