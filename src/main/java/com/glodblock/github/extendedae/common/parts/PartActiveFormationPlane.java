package com.glodblock.github.extendedae.common.parts;

import appeng.api.behaviors.PlacementStrategy;
import appeng.api.config.Actionable;
import appeng.api.config.FuzzyMode;
import appeng.api.config.Setting;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridNodeListener;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageService;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.api.stacks.AEKey;
import appeng.api.util.AECableType;
import appeng.api.util.IConfigManager;
import appeng.core.definitions.AEItems;
import appeng.core.settings.TickRates;
import appeng.helpers.IConfigInvHost;
import appeng.helpers.IPriorityHost;
import appeng.items.parts.PartModels;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import appeng.parts.PartModel;
import appeng.parts.automation.PlaneConnectionHelper;
import appeng.parts.automation.PlaneConnections;
import appeng.parts.automation.PlaneModelData;
import appeng.parts.automation.PlaneModels;
import appeng.parts.automation.StackWorldBehaviors;
import appeng.parts.automation.UpgradeablePart;
import appeng.util.ConfigInventory;
import appeng.util.Platform;
import appeng.util.prioritylist.IPartitionList;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.container.ContainerActiveFormationPlane;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class PartActiveFormationPlane extends UpgradeablePart implements IGridTickable, IPriorityHost, IConfigInvHost {

    public static final List<ResourceLocation> MODELS = List.of(
            ExtendedAE.id("part/active_formation_plane"),
            ExtendedAE.id("part/active_formation_plane_on")
    );

    @PartModels
    public static final IPartModel MODELS_OFF = new PartModel(MODELS.get(0), PlaneModels.MODEL_CHASSIS_OFF);

    @PartModels
    public static final IPartModel MODELS_ON = new PartModel(MODELS.get(0), PlaneModels.MODEL_CHASSIS_ON);

    @PartModels
    public static final IPartModel MODELS_HAS_CHANNEL = new PartModel(MODELS.get(1), PlaneModels.MODEL_CHASSIS_HAS_CHANNEL);

    private boolean wasOnline = false;
    private int priority = 0;
    private final PlaneConnectionHelper connectionHelper = new PlaneConnectionHelper(this);
    private final ConfigInventory config;
    @Nullable
    private PlacementStrategy placementStrategies;

    public PartActiveFormationPlane(IPartItem<?> partItem) {
        super(partItem);
        this.getMainNode().addService(IGridTickable.class, this);
        this.config = ConfigInventory.configTypes(63)
                .supportedTypes(StackWorldBehaviors.withPlacementStrategy())
                .build();
        this.getConfigManager().registerSetting(Settings.PLACE_BLOCK, YesNo.YES);
        this.getConfigManager().registerSetting(Settings.FUZZY_MODE, FuzzyMode.IGNORE_ALL);
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
            placementStrategies = StackWorldBehaviors.createPlacementStrategies(
                    (ServerLevel) self.getLevel(), pos, side, self, owningPlayerId);
        }
        return placementStrategies;
    }

    @Override
    protected int getUpgradeSlots() {
        return 5;
    }

    @Override
    public void onSettingChanged(IConfigManager manager, Setting<?> setting) {
        this.getHost().markForSave();
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

    @Override
    public void onNeighborChanged(BlockGetter level, BlockPos pos, BlockPos neighbor) {
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

    @Override
    public float getCableConnectionLength(AECableType cable) {
        return 1;
    }

    @Override
    public void readFromNBT(CompoundTag data) {
        super.readFromNBT(data);
        this.priority = data.getInt("priority");
        this.config.readFromChildTag(data, "config");
    }

    @Override
    public void writeToNBT(CompoundTag data) {
        super.writeToNBT(data);
        data.putInt("priority", this.getPriority());
        this.config.writeToChildTag(data, "config");
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

    private IPartitionList createFilter() {
        var builder = IPartitionList.builder();
        if (isUpgradedWith(AEItems.FUZZY_CARD)) {
            builder.fuzzyMode(getConfigManager().getSetting(Settings.FUZZY_MODE));
        }
        var slotsToUse = 18 + this.getInstalledUpgrades(AEItems.CAPACITY_CARD) * 9;
        for (var x = 0; x < this.config.size() && x < slotsToUse; x++) {
            builder.add(this.config.getKey(x));
        }
        return builder.build();
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        return new TickingRequest(TickRates.ExportBus.getMin(), TickRates.ExportBus.getMax(), isSleeping());
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        if (isSleeping()) {
            return TickRateModulation.SLEEP;
        }

        if (!canWork()) {
            return TickRateModulation.IDLE;
        }

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
            return block.canBeReplaced();
        }
        return Platform.areBlockEntitiesTicking(self.getLevel(), targetPos);
    }

    protected int availableSlots() {
        return Math.min(18 + getInstalledUpgrades(AEItems.CAPACITY_CARD) * 9, this.getConfig().size());
    }

    protected boolean doWork(IGrid grid) {
        var storageService = grid.getStorageService();
        var fzMode = this.getConfigManager().getSetting(Settings.FUZZY_MODE);

        int x;
        for (x = 0; x < availableSlots(); x ++) {
            var what = getConfig().getKey(x);
            if (what == null) {
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
        }
        return false;
    }

    private boolean isSuccess(IStorageService storageService, AEKey what) {
        var toExt = storageService.getInventory().extract(what, what.getAmountPerUnit(), Actionable.MODULATE, IActionSource.ofMachine(this));
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
    public boolean onPartActivate(Player player, InteractionHand hand, Vec3 pos) {
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
    public IPartModel getStaticModels() {
        if (this.isActive() && this.isPowered()) {
            return MODELS_HAS_CHANNEL;
        } else if (this.isPowered()) {
            return MODELS_ON;
        } else {
            return MODELS_OFF;
        }
    }

    @Override
    public ModelData getModelData() {
        return ModelData.builder()
                .with(PlaneModelData.CONNECTIONS, getConnections())
                .build();
    }

}