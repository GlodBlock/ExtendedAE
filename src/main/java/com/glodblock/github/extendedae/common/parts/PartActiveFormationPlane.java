package com.glodblock.github.extendedae.common.parts;

import appeng.api.behaviors.PlacementStrategy;
import appeng.api.config.Actionable;
import appeng.api.config.FuzzyMode;
import appeng.api.config.IncludeExclude;
import appeng.api.config.RedstoneMode;
import appeng.api.config.Setting;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridNodeListener;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.networking.crafting.ICraftingRequester;
import appeng.api.networking.crafting.ICraftingService;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageService;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartItem;
import appeng.api.stacks.AEKey;
import appeng.api.util.AECableType;
import appeng.api.util.IConfigManager;
import appeng.api.util.IConfigManagerBuilder;
import appeng.core.definitions.AEItems;
import appeng.core.settings.TickRates;
import appeng.helpers.IConfigInvHost;
import appeng.helpers.IPriorityHost;
import appeng.helpers.MultiCraftingTracker;
import appeng.me.helpers.MachineSource;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import appeng.parts.automation.PartModelData;
import appeng.parts.automation.PlaneConnectionHelper;
import appeng.parts.automation.PlaneConnections;
import appeng.parts.automation.StackWorldBehaviors;
import appeng.parts.automation.UpgradeablePart;
import appeng.util.ConfigInventory;
import appeng.util.Platform;
import appeng.util.prioritylist.IPartitionList;
import com.glodblock.github.extendedae.container.ContainerActiveFormationPlane;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class PartActiveFormationPlane extends UpgradeablePart implements IGridTickable, IPriorityHost, IConfigInvHost, ICraftingRequester {

    private boolean wasOnline = false;
    private int priority = 0;
    private final PlaneConnectionHelper connectionHelper = new PlaneConnectionHelper(this);
    private final ConfigInventory config;
    @Nullable
    private PlacementStrategy placementStrategies;
    private final MultiCraftingTracker craftingTracker;
    protected final IActionSource source;
    private boolean lastRedstone = false;
    private boolean pendingPulse = false;
    private IPartitionList filter;

    public PartActiveFormationPlane(IPartItem<?> partItem) {
        super(partItem);
        this.getMainNode()
                .addService(IGridTickable.class, this)
                .addService(ICraftingRequester.class, this);
        this.config = ConfigInventory.configTypes(63)
                .supportedTypes(StackWorldBehaviors.withPlacementStrategy())
                .changeListener(this::updateFilter)
                .build();
        this.source = new MachineSource(this);
        this.craftingTracker = new MultiCraftingTracker(this, this.config.size());
    }

    @Override
    protected void registerSettings(IConfigManagerBuilder builder) {
        super.registerSettings(builder);
        builder.registerSetting(Settings.PLACE_BLOCK, YesNo.YES);
        builder.registerSetting(Settings.FUZZY_MODE, FuzzyMode.IGNORE_ALL);
        builder.registerSetting(Settings.CRAFT_ONLY, YesNo.NO);
        builder.registerSetting(Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE);
    }

    @Override
    public RedstoneMode getRSMode() {
        return this.getConfigManager().getSetting(Settings.REDSTONE_CONTROLLED);
    }

    private boolean isInPulseMode() {
        return getRSMode() == RedstoneMode.SIGNAL_PULSE;
    }

    protected final PlacementStrategy getPlacementStrategies() {
        if (placementStrategies == null) {
            // Defer initialization until the grid exists
            var node = getMainNode().getNode();
            if (node == null) {
                return PlacementStrategy.noop();
            }
            var self = this.getHost().getBlockEntity();
            var pos = self.getBlockPos().relative(this.getSide());
            var side = getSide().getOpposite();
            var owningPlayerId = getMainNode().getNode().getOwningPlayerProfileId();
            placementStrategies = StackWorldBehaviors.createPlacementStrategies((ServerLevel) self.getLevel(), pos, side, self, owningPlayerId);
        }
        return placementStrategies;
    }

    protected final void updateFilter() {
        this.filter = createFilter();
    }

    @Override
    public void upgradesChanged() {
        this.updateFilter();
    }

    private IPartitionList createFilter() {
        var builder = IPartitionList.builder();
        if (this.isUpgradedWith(AEItems.FUZZY_CARD)) {
            builder.fuzzyMode(getConfigManager().getSetting(Settings.FUZZY_MODE));
        }
        var slotsToUse = 18 + this.getInstalledUpgrades(AEItems.CAPACITY_CARD) * 9;
        for (var x = 0; x < this.config.size() && x < slotsToUse; x++) {
            builder.add(this.config.getKey(x));
        }
        return builder.build();
    }

    private IPartitionList getFilter() {
        if (this.filter == null) {
            this.updateFilter();
        }
        return this.filter;
    }

    @Override
    protected int getUpgradeSlots() {
        return 5;
    }

    @Override
    public void onSettingChanged(IConfigManager manager, Setting<?> setting) {
        this.getHost().markForSave();
        updateRedstoneState();
        if (isInPulseMode()) {
            this.lastRedstone = getHost().hasRedstone();
        }
    }

    @Override
    public void addToWorld() {
        super.addToWorld();
        this.lastRedstone = this.getHost().hasRedstone();
        if (this.pendingPulse) {
            getMainNode().ifPresent((grid, node) -> grid.getTickManager().alertDevice(node));
        }
    }


    @Override
    protected void onMainNodeStateChanged(IGridNodeListener.State reason) {
        var currentOnline = this.getMainNode().isOnline();
        if (this.wasOnline != currentOnline) {
            this.wasOnline = currentOnline;
            this.getHost().markForUpdate();
        }
    }

    @Override
    public void getBoxes(IPartCollisionHelper bch) {
        connectionHelper.getBoxes(bch);
    }

    public PlaneConnections getConnections() {
        return connectionHelper.getConnections();
    }

    private void updateRedstoneState() {
        // Clear the pending pulse flag if the upgrade is removed or the config is toggled off
        if (!this.isInPulseMode()) {
            this.pendingPulse = false;
        }

        getMainNode().ifPresent((grid, node) -> {
            if (!this.isSleeping()) {
                grid.getTickManager().wakeDevice(node);
            } else {
                grid.getTickManager().sleepDevice(node);
            }
        });
    }

    @Override
    public void onNeighborChanged(BlockGetter level, BlockPos pos, BlockPos neighbor) {
        if (isInPulseMode()) {
            var hostIsPowered = this.getHost().hasRedstone();
            if (this.lastRedstone != hostIsPowered) {
                this.lastRedstone = hostIsPowered;
                if (this.lastRedstone && !this.pendingPulse) {
                    this.pendingPulse = true;
                    getMainNode().ifPresent((grid, node) -> grid.getTickManager().alertDevice(node));
                }
            }
        } else {
            updateRedstoneState();
        }
        if (pos.relative(this.getSide()).equals(neighbor)) {
            // The neighbor this plane is facing has changed
            if (!isClientSide()) {
                getPlacementStrategies().clearBlocked();
            }
        } else {
            connectionHelper.updateConnections();
        }
    }

    @Override
    public void onUpdateShape(Direction side) {
        var ourSide = getSide();
        // A block might have been changed in front of us
        if (side.equals(ourSide)) {
            if (!isClientSide()) {
                getPlacementStrategies().clearBlocked();
            }
        } else if (ourSide.getAxis() != side.getAxis()) {
            // Changes perpendicular to our side may change the connected plane model to change
            connectionHelper.updateConnections();
        }
    }

    protected long placeInWorld(AEKey what, long amount) {
        var placeBlock = this.getConfigManager().getSetting(Settings.PLACE_BLOCK);
        return getPlacementStrategies().placeInWorld(what, amount, Actionable.MODULATE, placeBlock != YesNo.YES);
    }

    protected long placeInWorld(AEKey what, long amount, Actionable mode) {
        var placeBlock = this.getConfigManager().getSetting(Settings.PLACE_BLOCK);
        return getPlacementStrategies().placeInWorld(what, amount, mode, placeBlock != YesNo.YES);
    }

    @Override
    public float getCableConnectionLength(AECableType cable) {
        return 1;
    }

    @Override
    public void readFromNBT(ValueInput data) {
        super.readFromNBT(data);
        this.priority = data.getIntOr("priority", 0);
        this.config.readFromChildTag(data, "config");
        this.craftingTracker.readFromNBT(data);
        this.pendingPulse = isInPulseMode() && data.getBooleanOr("pendingPulse", false);
    }

    @Override
    public void writeToNBT(ValueOutput data) {
        super.writeToNBT(data);
        data.putInt("priority", this.getPriority());
        this.config.writeToChildTag(data, "config");
        this.craftingTracker.writeToNBT(data);
        if (isInPulseMode() && this.pendingPulse) {
            data.putBoolean("pendingPulse", true);
        }
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public void setPriority(int newValue) {
        this.priority = newValue;
        this.getHost().markForSave();
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu subMenu) {
        MenuOpener.returnTo(getMenuType(), player, MenuLocators.forPart(this));
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return new ItemStack(getPartItem());
    }

    private void openConfigMenu(Player player) {
        MenuOpener.open(getMenuType(), player, MenuLocators.forPart(this));
    }

    protected MenuType<?> getMenuType() {
        return ContainerActiveFormationPlane.TYPE;
    }

    @Override
    protected boolean isSleeping() {
        if (isInPulseMode() && this.pendingPulse) {
            return false;
        } else {
            return super.isSleeping();
        }
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        return new TickingRequest(2, TickRates.ExportBus.getMax(), isSleeping());
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        if (isSleeping()) {
            return TickRateModulation.SLEEP;
        }

        if (!canWork()) {
            return TickRateModulation.IDLE;
        }
        this.pendingPulse = false;
        var hasDoneWork = this.doWork(node.getGrid());

        return hasDoneWork ? TickRateModulation.FASTER : TickRateModulation.SLOWER;
    }

    protected final boolean canWork() {
        if (!getMainNode().isActive()) {
            return false;
        }
        var self = this.getHost().getBlockEntity();
        var targetPos = self.getBlockPos().relative(getSide());
        if (self.getLevel() == null) {
            return false;
        }
        if (this.getConfigManager().getSetting(Settings.PLACE_BLOCK) == YesNo.YES) {
            var block = self.getLevel().getBlockState(targetPos);
            return block.isAir();
        }
        return Platform.areBlockEntitiesTicking(self.getLevel(), targetPos);
    }

    protected int availableSlots() {
        return Math.min(18 + getInstalledUpgrades(AEItems.CAPACITY_CARD) * 9, this.getConfig().size());
    }

    protected boolean doWork(IGrid grid) {
        var storageService = grid.getStorageService();
        var fzMode = this.getConfigManager().getSetting(Settings.FUZZY_MODE);
        var filterMode = isUpgradedWith(AEItems.INVERTER_CARD) ? IncludeExclude.BLACKLIST : IncludeExclude.WHITELIST;
        var cg = grid.getCraftingService();

        int x;
        if (filterMode == IncludeExclude.WHITELIST) {
            for (x = 0; x < availableSlots(); x ++) {
                var what = getConfig().getKey(x);
                if (what == null) {
                    continue;
                }
                if (this.craftOnly()) {
                    attemptCrafting(cg, x, what);
                    continue;
                }
                if (isUpgradedWith(AEItems.FUZZY_CARD)) {
                    for (var fuzzyWhat : ImmutableList.copyOf(storageService.getCachedInventory().findFuzzy(what, fzMode))) {
                        if (isSuccess(storageService, fuzzyWhat.getKey())) {
                            return true;
                        }
                    }
                } else {
                    if (isSuccess(storageService, what)) {
                        return true;
                    }
                }
                if (this.isCraftingEnabled()) {
                    attemptCrafting(cg, x, what);
                }
            }
        } else {
            for (var what : storageService.getCachedInventory().keySet()) {
                if (this.getFilter().matchesFilter(what, IncludeExclude.BLACKLIST)) {
                    if (isSuccess(storageService, what)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected long getDropMultiplier() {
        return switch (getInstalledUpgrades(AEItems.SPEED_CARD)) {
            case 1 -> 8;
            case 2 -> 32;
            case 3 -> 64;
            case 4 -> 96;
            default -> 1;
        };
    }

    protected long getExtractAmount(AEKey what) {
        if (this.getConfigManager().getSetting(Settings.PLACE_BLOCK) == YesNo.NO) {
            return this.getDropMultiplier() * what.getAmountPerOperation();
        }
        return what.getAmountPerUnit();
    }

    private boolean isSuccess(IStorageService storageService, AEKey what) {
        var toExt = storageService.getInventory().extract(what, this.getExtractAmount(what), Actionable.MODULATE, IActionSource.ofMachine(this));
        if (toExt > 0) {
            var res = placeInWorld(what, toExt);
            var differ = toExt - res;
            if (differ > 0) {
                storageService.getInventory().insert(what, differ, Actionable.MODULATE, IActionSource.ofMachine(this));
            }
            return res > 0;
        }
        return false;
    }

    @Override
    public boolean onUseWithoutItem(Player player, Vec3 pos) {
        if (!isClientSide()) {
            openConfigMenu(player);
        }
        return true;
    }

    @Override
    public ConfigInventory getConfig() {
        return config;
    }

    @Override
    public void collectModelData(ModelData.Builder builder) {
        super.collectModelData(builder);
        builder.with(PartModelData.CONNECTIONS, getConnections());
    }

    @Override
    public ImmutableSet<@NotNull ICraftingLink> getRequestedJobs() {
        return this.craftingTracker.getRequestedJobs();
    }

    @Override
    public long insertCraftedItems(ICraftingLink link, AEKey what, long amount, Actionable mode) {
        var grid = getMainNode().getGrid();
        if (grid != null && getMainNode().isActive()) {
            return this.placeInWorld(what, amount, mode);
        }
        return 0;
    }

    @Override
    public void jobStateChange(ICraftingLink link) {
        this.craftingTracker.jobStateChange(link);
    }

    private void attemptCrafting(ICraftingService cg, int slotToExport, AEKey what) {
        var amount = placeInWorld(what, what.getAmountPerUnit(), Actionable.SIMULATE);
        if (amount > 0) {
            requestCrafting(cg, slotToExport, what, amount);
        }
    }

    protected final void requestCrafting(ICraftingService cg, int configSlot, AEKey what, long amount) {
        this.craftingTracker.handleCrafting(configSlot, what, amount, this.getBlockEntity().getLevel(), cg, this.source);
    }

    private boolean craftOnly() {
        return isCraftingEnabled() && this.getConfigManager().getSetting(Settings.CRAFT_ONLY) == YesNo.YES;
    }

    private boolean isCraftingEnabled() {
        return isUpgradedWith(AEItems.CRAFTING_CARD);
    }

}