package com.glodblock.github.extendedae.common.parts;

import appeng.api.behaviors.PickupStrategy;
import appeng.api.config.Actionable;
import appeng.api.config.FuzzyMode;
import appeng.api.config.IncludeExclude;
import appeng.api.config.Setting;
import appeng.api.config.Settings;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.storage.StorageHelper;
import appeng.api.util.AECableType;
import appeng.api.util.IConfigManager;
import appeng.api.util.IConfigManagerBuilder;
import appeng.core.AEConfig;
import appeng.core.definitions.AEItems;
import appeng.core.settings.TickRates;
import appeng.helpers.IConfigInvHost;
import appeng.items.parts.PartModels;
import appeng.me.helpers.MachineSource;
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
import appeng.util.SettingsFrom;
import appeng.util.prioritylist.IPartitionList;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.container.ContainerSmartAnnihilationPlane;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings({"SequencedCollectionMethodCanBeUsed", "UnstableApiUsage"})
public class PartSmartAnnihilationPlane extends UpgradeablePart implements IGridTickable, IConfigInvHost {

    public static final List<Identifier> MODELS = List.of(
            ExtendedAE.id("part/smart_annihilation_plane"),
            ExtendedAE.id("part/smart_annihilation_plane_on")
    );

    @PartModels
    public static final IPartModel MODELS_OFF = new PartModel(MODELS.get(0), PlaneModels.MODEL_CHASSIS_OFF);

    @PartModels
    public static final IPartModel MODELS_ON = new PartModel(MODELS.get(0), PlaneModels.MODEL_CHASSIS_ON);

    @PartModels
    public static final IPartModel MODELS_HAS_CHANNEL = new PartModel(MODELS.get(1), PlaneModels.MODEL_CHASSIS_HAS_CHANNEL);

    private final PlaneConnectionHelper connectionHelper = new PlaneConnectionHelper(this);
    private final ConfigInventory config;
    @Nullable
    private List<PickupStrategy> pickupStrategies;
    private ItemEnchantments enchantments = ItemEnchantments.EMPTY;
    private int continuousGenerationTicks;
    private ContinuousGeneration continuousGeneration;
    private final IActionSource actionSource = new MachineSource(this);
    private IPartitionList filter;
    private IncludeExclude listMode = IncludeExclude.WHITELIST;

    public PartSmartAnnihilationPlane(IPartItem<?> partItem) {
        super(partItem);
        this.getMainNode().addService(IGridTickable.class, this);
        this.config = ConfigInventory.configTypes(63)
                .supportedTypes(AEKeyType.items(), AEKeyType.fluids())
                .changeListener(this::updateFilter)
                .build();
    }

    private void updateFilter() {
        boolean isFuzzy = this.getUpgrades().isInstalled(AEItems.FUZZY_CARD);
        var builder = IPartitionList.builder();
        builder.addAll(this.config.keySet());
        if (isFuzzy) {
            builder.fuzzyMode(getConfigManager().getSetting(Settings.FUZZY_MODE));
        }
        this.filter = builder.build();
        this.listMode = this.getUpgrades().isInstalled(AEItems.INVERTER_CARD) ? IncludeExclude.BLACKLIST : IncludeExclude.WHITELIST;
        this.refresh();
    }

    @Override
    public void upgradesChanged() {
        this.updateFilter();
    }

    private IPartitionList getFilter() {
        if (this.filter == null) {
            this.updateFilter();
        }
        return this.filter;
    }

    @Override
    protected void registerSettings(IConfigManagerBuilder builder) {
        super.registerSettings(builder);
        builder.registerSetting(Settings.FUZZY_MODE, FuzzyMode.IGNORE_ALL);
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
    public void addToWorld() {
        super.addToWorld();
        var host = getBlockEntity();
        var buildHeight = host.getLevel().getMaxBuildHeight();
        continuousGenerationTicks = 0;
        // When placed at max build height facing up, continuously generate 1 sky stone dust / 10 seconds
        if (AEConfig.instance().isAnnihilationPlaneSkyDustGenerationEnabled() && host.getBlockPos().getY() + 1 >= buildHeight && getSide() == Direction.UP) {
            continuousGeneration = new ContinuousGeneration(AEItemKey.of(AEItems.SKY_DUST), 1, 200);
        }
    }

    @Override
    public void readFromNBT(CompoundTag data, HolderLookup.Provider registries) {
        super.readFromNBT(data, registries);
        this.config.readFromChildTag(data, "config", registries);
        if (data.contains("enchantments")) {
            var enchantmentsTag = data.getCompound("enchantments");
            var ops = registries.createSerializationContext(NbtOps.INSTANCE);
            this.enchantments = ItemEnchantments.CODEC.decode(ops, enchantmentsTag)
                    .ifError(err -> ExtendedAE.LOGGER.warn("Failed to load enchantments for part {}: {}", this, err.message()))
                    .getOrThrow()
                    .getFirst();
        }
    }

    @Override
    public void writeToNBT(CompoundTag data, HolderLookup.Provider registries) {
        super.writeToNBT(data, registries);
        this.config.writeToChildTag(data, "config", registries);
        var ops = registries.createSerializationContext(NbtOps.INSTANCE);
        var enchantmentsTag = ItemEnchantments.CODEC.encodeStart(ops, this.enchantments).getOrThrow();
        if (enchantmentsTag instanceof CompoundTag compoundTag && !compoundTag.isEmpty()) {
            data.put("enchantments", enchantmentsTag);
        }
    }

    @Override
    public void importSettings(SettingsFrom mode, DataComponentMap data, @Nullable Player player) {
        super.importSettings(mode, data, player);
        // Import enchants only when the plan is placed, not from memory cards
        if (mode == SettingsFrom.DISMANTLE_ITEM) {
            this.enchantments = data.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        }
        pickupStrategies = null;
    }

    @Override
    public void exportSettings(SettingsFrom mode, DataComponentMap.Builder data) {
        super.exportSettings(mode, data);
        // Save enchants only when the actual plane is dismantled
        if (mode == SettingsFrom.DISMANTLE_ITEM) {
            data.set(DataComponents.ENCHANTMENTS, this.enchantments);
        }
    }

    @Override
    public boolean onUseWithoutItem(Player player, Vec3 pos) {
        if (!isClientSide()) {
            openConfigMenu(player);
        }
        return true;
    }

    private void openConfigMenu(Player player) {
        MenuOpener.open(getMenuType(), player, MenuLocators.forPart(this));
    }

    protected MenuType<?> getMenuType() {
        return ContainerSmartAnnihilationPlane.TYPE;
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        return new TickingRequest(TickRates.AnnihilationPlane, false);
    }

    @Override
    public float getCableConnectionLength(AECableType cable) {
        return 1;
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        if (!isActive()) {
            return TickRateModulation.SLEEP;
        }

        var grid = node.getGrid();

        if (continuousGeneration != null) {
            continuousGenerationTicks += ticksSinceLastCall;
            if (continuousGenerationTicks >= continuousGeneration.ticks) {
                long amount = continuousGenerationTicks / continuousGeneration.ticks;
                insertIntoGrid(continuousGeneration.what, amount, Actionable.MODULATE);
                continuousGenerationTicks -= (int) (amount * continuousGeneration.ticks);
            }
            return TickRateModulation.IDLE;
        }

        // Reset to allow more entity pickups
        for (var pickupStrategy : getPickupStrategies()) {
            pickupStrategy.reset();
        }

        for (PickupStrategy pickupStrategy : getPickupStrategies()) {
            var pickupResult = pickupStrategy.tryPickup(grid.getEnergyService(), this::insertIntoGrid);

            if (pickupResult == PickupStrategy.Result.PICKED_UP) {
                return TickRateModulation.URGENT;
            } else if (pickupResult == PickupStrategy.Result.CANT_STORE) {
                return TickRateModulation.IDLE;
            }
        }

        return TickRateModulation.SLEEP;
    }

    private long insertIntoGrid(AEKey what, long amount, Actionable mode) {
        var grid = getMainNode().getGrid();
        if (grid == null) {
            return 0;
        }
        if (this.getFilter().matchesFilter(what, this.listMode)) {
            return StorageHelper.poweredInsert(grid.getEnergyService(), grid.getStorageService().getInventory(), what, amount, this.actionSource, mode);
        }
        return 0;
    }

    @Override
    public void getBoxes(IPartCollisionHelper bch) {
        if (bch.isBBCollision()) {
            bch.addBox(0, 0, 14, 16, 16, 15.5);
            return;
        }
        connectionHelper.getBoxes(bch);
    }

    public PlaneConnections getConnections() {
        return connectionHelper.getConnections();
    }

    @Override
    public void onNeighborChanged(BlockGetter level, BlockPos pos, BlockPos neighbor) {
        if (pos.relative(this.getSide()).equals(neighbor)) {
            if (!isClientSide()) {
                this.refresh();
            }
        }
    }

    @Override
    public void onUpdateShape(Direction side) {
        var ourSide = getSide();
        // A block might have been placed in front of us
        if (side.equals(ourSide)) {
            if (!isClientSide()) {
                this.refresh();
            }
        } else if (ourSide.getAxis() != side.getAxis()) {
            // Changes perpendicular to our side may change the connected plane model to change
            connectionHelper.updateConnections();
        }
    }

    @Override
    public void onEntityCollision(Entity entity) {
        if (!entity.isAlive() || isClientSide() || !this.getMainNode().isActive()) {
            return;
        }
        var grid = getMainNode().getGrid();
        if (grid == null) {
            return;
        }
        PickupStrategy strategy = null;
        for (PickupStrategy pickupStrategy : getPickupStrategies()) {
            if (pickupStrategy.canPickUpEntity(entity)) {
                strategy = pickupStrategy;
                break;
            }
        }
        if (strategy == null) {
            return;
        }
        var pos = getHost().getBlockEntity().getBlockPos();
        var planePosX = pos.getX();
        var planePosY = pos.getY();
        var planePosZ = pos.getZ();

        // This is the middle point of the entities BB, which is better suited for comparisons
        // that don't rely on it "touching" the plane
        var posYMiddle = (entity.getBoundingBox().minY + entity.getBoundingBox().maxY) / 2.0D;
        var entityPosX = entity.getX();
        var entityPosY = entity.getY();
        var entityPosZ = entity.getZ();

        var captureX = entityPosX > planePosX && entityPosX < planePosX + 1;
        var captureY = posYMiddle > planePosY && posYMiddle < planePosY + 1;
        var captureZ = entityPosZ > planePosZ && entityPosZ < planePosZ + 1;

        var capture = switch (getSide()) {
            case DOWN -> captureX && captureZ && entityPosY < planePosY + 0.1;
            case UP -> captureX && captureZ && entityPosY > planePosY + 0.9;
            case SOUTH -> captureX && captureY && entityPosZ > planePosZ + 0.9;
            case NORTH -> captureX && captureY && entityPosZ < planePosZ + 0.1;
            case EAST -> captureZ && captureY && entityPosX > planePosX + 0.9;
            case WEST -> captureZ && captureY && entityPosX < planePosX + 0.1;
        };

        if (capture) {
            if (!strategy.pickUpEntity(grid.getEnergyService(), this::insertIntoGrid, entity)) {
                getMainNode().ifPresent((g, n) -> g.getTickManager().alertDevice(n));
            }
        }
    }

    protected List<PickupStrategy> getPickupStrategies() {
        if (pickupStrategies == null) {
            var node = getMainNode().getNode();
            if (node == null) {
                return List.of();
            }
            var self = this.getHost().getBlockEntity();
            var pos = self.getBlockPos().relative(this.getSide());
            var side = getSide().getOpposite();
            var owner = node.getOwningPlayerProfileId();
            if (this.enchantments == null) {
                this.enchantments = ItemEnchantments.EMPTY;
            }
            pickupStrategies = StackWorldBehaviors.createPickupStrategies((ServerLevel) self.getLevel(), pos, side, self, enchantments, owner);
        }
        return pickupStrategies;
    }

    private void refresh() {
        for (var pickupStrategy : getPickupStrategies()) {
            pickupStrategy.reset();
        }
        getMainNode().ifPresent((g, n) -> g.getTickManager().alertDevice(n));
    }

    @Override
    public ConfigInventory getConfig() {
        return this.config;
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
        return ModelData.builder().with(PlaneModelData.CONNECTIONS, getConnections()).build();
    }

    public ItemEnchantments getEnchantments() {
        return this.enchantments;
    }

    private record ContinuousGeneration(AEKey what, long amount, int ticks) {

    }

}
