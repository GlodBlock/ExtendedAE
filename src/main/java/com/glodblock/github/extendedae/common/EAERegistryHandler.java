package com.glodblock.github.extendedae.common;

import appeng.api.AECapabilities;
import appeng.api.client.StorageCellModels;
import appeng.api.implementations.blockentities.ICraftingMachine;
import appeng.api.implementations.items.IAEItemPowerStorage;
import appeng.api.networking.IInWorldGridNodeHost;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.api.parts.RegisterPartCapabilitiesEvent;
import appeng.api.storage.StorageCells;
import appeng.api.upgrades.Upgrades;
import appeng.block.AEBaseBlockItem;
import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.AEBaseInvBlockEntity;
import appeng.blockentity.ClientTickingBlockEntity;
import appeng.blockentity.ServerTickingBlockEntity;
import appeng.blockentity.powersink.AEBasePoweredBlockEntity;
import appeng.core.definitions.AEItems;
import appeng.crafting.pattern.AECraftingPattern;
import appeng.crafting.pattern.AEProcessingPattern;
import appeng.crafting.pattern.AESmithingTablePattern;
import appeng.crafting.pattern.AEStonecuttingPattern;
import appeng.items.AEBaseItem;
import appeng.items.parts.PartItem;
import appeng.items.tools.powered.powersink.PoweredItemCapabilities;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.api.caps.ICrankPowered;
import com.glodblock.github.extendedae.api.caps.IGenericInvHost;
import com.glodblock.github.extendedae.api.caps.IMEStorageAccess;
import com.glodblock.github.extendedae.common.inventory.InfinityCellInventory;
import com.glodblock.github.extendedae.common.inventory.VoidCellInventory;
import com.glodblock.github.extendedae.common.items.ItemMEPackingTape;
import com.glodblock.github.extendedae.common.parts.PartExInterface;
import com.glodblock.github.extendedae.common.parts.PartExPatternProvider;
import com.glodblock.github.extendedae.common.parts.PartOversizeInterface;
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixBase;
import com.glodblock.github.extendedae.config.ConfigCondition;
import com.glodblock.github.extendedae.config.EAEConfig;
import com.glodblock.github.extendedae.container.ContainerActiveFormationPlane;
import com.glodblock.github.extendedae.container.ContainerAssemblerMatrix;
import com.glodblock.github.extendedae.container.ContainerCaner;
import com.glodblock.github.extendedae.container.ContainerCircuitCutter;
import com.glodblock.github.extendedae.container.ContainerConfigModifier;
import com.glodblock.github.extendedae.container.ContainerCrystalAssembler;
import com.glodblock.github.extendedae.container.ContainerExDrive;
import com.glodblock.github.extendedae.container.ContainerExIOBus;
import com.glodblock.github.extendedae.container.ContainerExIOPort;
import com.glodblock.github.extendedae.container.ContainerExInterface;
import com.glodblock.github.extendedae.container.ContainerExMolecularAssembler;
import com.glodblock.github.extendedae.container.ContainerExPatternProvider;
import com.glodblock.github.extendedae.container.ContainerExPatternTerminal;
import com.glodblock.github.extendedae.container.ContainerIngredientBuffer;
import com.glodblock.github.extendedae.container.ContainerModExportBus;
import com.glodblock.github.extendedae.container.ContainerModStorageBus;
import com.glodblock.github.extendedae.container.ContainerPatternModifier;
import com.glodblock.github.extendedae.container.ContainerPreciseExportBus;
import com.glodblock.github.extendedae.container.ContainerPreciseStorageBus;
import com.glodblock.github.extendedae.container.ContainerRenamer;
import com.glodblock.github.extendedae.container.ContainerSmartAnnihilationPlane;
import com.glodblock.github.extendedae.container.ContainerTagExportBus;
import com.glodblock.github.extendedae.container.ContainerTagStorageBus;
import com.glodblock.github.extendedae.container.ContainerThresholdExportBus;
import com.glodblock.github.extendedae.container.ContainerThresholdLevelEmitter;
import com.glodblock.github.extendedae.container.ContainerVoidCell;
import com.glodblock.github.extendedae.container.ContainerWirelessConnector;
import com.glodblock.github.extendedae.container.ContainerWirelessHub;
import com.glodblock.github.extendedae.container.pattern.ContainerCraftingPattern;
import com.glodblock.github.extendedae.container.pattern.ContainerProcessingPattern;
import com.glodblock.github.extendedae.container.pattern.ContainerSmithingTablePattern;
import com.glodblock.github.extendedae.container.pattern.ContainerStonecuttingPattern;
import com.glodblock.github.extendedae.container.pattern.PatternGuiHandler;
import com.glodblock.github.extendedae.recipe.CircuitCutterRecipe;
import com.glodblock.github.extendedae.recipe.CircuitCutterRecipeSerializer;
import com.glodblock.github.extendedae.recipe.CrystalAssemblerRecipe;
import com.glodblock.github.extendedae.recipe.CrystalAssemblerRecipeSerializer;
import com.glodblock.github.extendedae.recipe.CrystalFixerRecipe;
import com.glodblock.github.extendedae.recipe.CrystalFixerRecipeSerializer;
import com.glodblock.github.extendedae.util.CacheHolder;
import com.glodblock.github.glodium.registry.RegistryHandler;
import com.glodblock.github.glodium.registry.defer.DeferredTileEntityType;
import com.glodblock.github.glodium.registry.token.TileToken;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

public class EAERegistryHandler extends RegistryHandler {

    public static EAERegistryHandler INSTANCE;

    @SuppressWarnings("UnstableApiUsage")
    public EAERegistryHandler(IEventBus modBus) {
        super(ExtendedAE.MODID, modBus);
        modBus.addListener(this::onRegisterEvent);
        this.cap(AEBaseInvBlockEntity.class, Capabilities.Item.BLOCK, AEBaseInvBlockEntity::getExposedItemHandler);
        this.cap(AEBasePoweredBlockEntity.class, Capabilities.Energy.BLOCK, AEBasePoweredBlockEntity::getEnergyStorage);
        this.cap(IInWorldGridNodeHost.class, AECapabilities.IN_WORLD_GRID_NODE_HOST, (object, _) -> object);
        this.cap(IAEItemPowerStorage.class, Capabilities.Energy.ITEM, (object, context) -> new PoweredItemCapabilities(context, object.getItem(), (IAEItemPowerStorage) object.getItem()));
        this.cap(ICrankPowered.class, AECapabilities.CRANKABLE, ICrankPowered::getCrankable);
        this.cap(ICraftingMachine.class, AECapabilities.CRAFTING_MACHINE, (object, _) -> object);
        this.cap(IGenericInvHost.class, AECapabilities.GENERIC_INTERNAL_INV, IGenericInvHost::getGenericInv);
        this.cap(IMEStorageAccess.class, AECapabilities.ME_STORAGE, IMEStorageAccess::getMEStorage);
        this.cap(TileAssemblerMatrixBase.class, Capabilities.Item.BLOCK, TileAssemblerMatrixBase::getPatternInv);
    }

    public <T extends AEBaseBlockEntity, B extends AEBaseEntityBlock<T>> DeferredBlock<@NotNull B> block(String name, Function<BlockBehaviour.Properties, B> builder, Class<T> clazz, TileFactory<@NotNull T> supplier) {
        var aeBlock = block(name, builder, BlockBehaviour.Properties.of(), AEBaseBlockItem::new);
        tile(name, clazz, supplier, aeBlock);
        return aeBlock;
    }

    public <P extends IPart> DeferredItem<@NotNull PartItem<P>> item(String name, Class<P> partClass, Function<IPartItem<P>, P> factory) {
        return this.item(name, properties -> new PartItem<>(properties, partClass, factory));
    }

    public <T extends AEBaseBlockEntity> DeferredTileEntityType<T> tile(String name, Class<T> tileClass, TileFactory<@NotNull T> factory, DeferredBlock<? extends @NotNull AEBaseEntityBlock<T>> block) {
        return (DeferredTileEntityType<T>) this.tiles.register(name, () -> {
            CacheHolder<BlockEntityType<@NotNull T>> holder = CacheHolder.empty();
            BlockEntityType<@NotNull T> tileType = new BlockEntityType<>((pos, state) -> factory.create(holder.get(), pos, state), Set.of(block.get()));
            holder.update(tileType);
            BlockEntityTicker<@NotNull T> serverTicker = null;
            if (ServerTickingBlockEntity.class.isAssignableFrom(tileClass)) {
                serverTicker = (_, _, _, entity) -> ((ServerTickingBlockEntity) entity).serverTick();
            }
            BlockEntityTicker<@NotNull T> clientTicker = null;
            if (ClientTickingBlockEntity.class.isAssignableFrom(tileClass)) {
                clientTicker = (_, _, _, entity) -> ((ClientTickingBlockEntity) entity).clientTick();
            }
            block.get().setBlockEntity(tileClass, tileType, clientTicker, serverTicker);
            var token = new TileToken(holder::get, tileClass);
            this.tileTypes.add(token);
            this.tileBind.add(Pair.of(token, Set.of(block.get())));
            return tileType;
        });
    }

    public Collection<Block> getBlocks() {
        return this.blocks.getEntries().stream().map(DeferredHolder::get).map(b -> (Block) b).toList();
    }

    @SubscribeEvent
    public void onRegisterCapability(RegisterPartCapabilitiesEvent event) {
        PartExInterface.registerCapability(event);
        PartExPatternProvider.registerCapability(event);
        PartOversizeInterface.registerCapability(event);
    }

    private void onRegisterEvent(RegisterEvent e) {
        if (e.getRegistry().equals(BuiltInRegistries.RECIPE_TYPE)) {
            this.onRegisterRecipeType();
        } else if (e.getRegistry().equals(BuiltInRegistries.RECIPE_SERIALIZER)) {
            this.onRegisterRecipeSerializer();
        } else if (e.getRegistry().equals(NeoForgeRegistries.CONDITION_SERIALIZERS)) {
            this.onRegisterCondition();
        } else if (e.getRegistry().equals(BuiltInRegistries.MENU)) {
            this.onRegisterContainer();
        }
    }

    private void onRegisterRecipeType() {
        Registry.register(BuiltInRegistries.RECIPE_TYPE, CrystalAssemblerRecipe.ID, CrystalAssemblerRecipe.TYPE);
        Registry.register(BuiltInRegistries.RECIPE_TYPE, CircuitCutterRecipe.ID, CircuitCutterRecipe.TYPE);
        Registry.register(BuiltInRegistries.RECIPE_TYPE, CrystalFixerRecipe.ID, CrystalFixerRecipe.TYPE);
    }

    private void onRegisterRecipeSerializer() {
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, CrystalAssemblerRecipe.ID, CrystalAssemblerRecipeSerializer.INSTANCE);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, CircuitCutterRecipe.ID, CircuitCutterRecipeSerializer.INSTANCE);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, CrystalFixerRecipe.ID, CrystalFixerRecipeSerializer.INSTANCE);
    }

    private void onRegisterCondition() {
        Registry.register(NeoForgeRegistries.CONDITION_SERIALIZERS, ExtendedAE.id("config"), ConfigCondition.CODEC);
    }

    private void onRegisterContainer() {
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("ex_pattern_provider"), ContainerExPatternProvider.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("ex_interface"), ContainerExInterface.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("ex_export_bus"), ContainerExIOBus.EXPORT_TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("ex_import_bus"), ContainerExIOBus.IMPORT_TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("ex_pattern_access_terminal"), ContainerExPatternTerminal.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("wireless_connector"), ContainerWirelessConnector.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("ingredient_buffer"), ContainerIngredientBuffer.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("ex_drive"), ContainerExDrive.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("pattern_modifier"), ContainerPatternModifier.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("ex_molecular_assembler"), ContainerExMolecularAssembler.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("tag_storage_bus"), ContainerTagStorageBus.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("tag_export_bus"), ContainerTagExportBus.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("threshold_level_emitter"), ContainerThresholdLevelEmitter.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("renamer"), ContainerRenamer.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("mod_storage_bus"), ContainerModStorageBus.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("mod_export_bus"), ContainerModExportBus.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("active_formation_plane"), ContainerActiveFormationPlane.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("caner"), ContainerCaner.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("precise_export_bus"), ContainerPreciseExportBus.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("ex_ioport"), ContainerExIOPort.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("precise_storage_bus"), ContainerPreciseStorageBus.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("threshold_export_bus"), ContainerThresholdExportBus.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("crystal_assembler"), ContainerCrystalAssembler.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("circuit_cutter"), ContainerCircuitCutter.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("oversize_interface"), ContainerExInterface.TYPE_OVERSIZE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("assembler_matrix"), ContainerAssemblerMatrix.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("void_cell"), ContainerVoidCell.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("config_modifier"), ContainerConfigModifier.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("wireless_hub"), ContainerWirelessHub.TYPE);
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("smart_annihilation_plane"), ContainerSmartAnnihilationPlane.TYPE);
        Registry.register(BuiltInRegistries.MENU, ContainerProcessingPattern.ID, ContainerProcessingPattern.TYPE);
        Registry.register(BuiltInRegistries.MENU, ContainerCraftingPattern.ID, ContainerCraftingPattern.TYPE);
        Registry.register(BuiltInRegistries.MENU, ContainerStonecuttingPattern.ID, ContainerStonecuttingPattern.TYPE);
        Registry.register(BuiltInRegistries.MENU, ContainerSmithingTablePattern.ID, ContainerSmithingTablePattern.TYPE);
    }

    public void onInit() {
        for (var entry : this.blocks.getEntries()) {
            Block block = entry.value();
            if (block instanceof AEBaseEntityBlock<?>) {
                AEBaseBlockEntity.registerBlockEntityItem(
                        ((AEBaseEntityBlock<?>) block).getBlockEntityType(),
                        block.asItem()
                );
            }
        }
        this.registerAEUpgrade();
        this.registerStorageHandler();
        this.registerRandomAPI();
        this.initPackageList();
    }

    private void registerAEUpgrade() {
        Upgrades.add(AEItems.FUZZY_CARD, EAESingletons.EX_INTERFACE, 1, "gui.extendedae.ex_interface");
        Upgrades.add(AEItems.CRAFTING_CARD, EAESingletons.EX_INTERFACE, 1, "gui.extendedae.ex_interface");
        Upgrades.add(AEItems.FUZZY_CARD, EAESingletons.EX_INTERFACE_PART, 1, "gui.extendedae.ex_interface");
        Upgrades.add(AEItems.CRAFTING_CARD, EAESingletons.EX_INTERFACE_PART, 1, "gui.extendedae.ex_interface");
        Upgrades.add(AEItems.FUZZY_CARD, EAESingletons.OVERSIZE_INTERFACE, 1, "gui.extendedae.oversize_interface");
        Upgrades.add(AEItems.CRAFTING_CARD, EAESingletons.OVERSIZE_INTERFACE, 1, "gui.extendedae.oversize_interface");
        Upgrades.add(AEItems.FUZZY_CARD, EAESingletons.OVERSIZE_INTERFACE_PART, 1, "gui.extendedae.oversize_interface");
        Upgrades.add(AEItems.CRAFTING_CARD, EAESingletons.OVERSIZE_INTERFACE_PART, 1, "gui.extendedae.oversize_interface");
        Upgrades.add(AEItems.CAPACITY_CARD, EAESingletons.EX_EXPORT_BUS, 5, "group.ex_io_bus_part");
        Upgrades.add(AEItems.REDSTONE_CARD, EAESingletons.EX_EXPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.SPEED_CARD, EAESingletons.EX_EXPORT_BUS, 4, "group.ex_io_bus_part");
        Upgrades.add(AEItems.FUZZY_CARD, EAESingletons.EX_EXPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.CRAFTING_CARD, EAESingletons.EX_EXPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.CAPACITY_CARD, EAESingletons.EX_IMPORT_BUS, 5, "group.ex_io_bus_part");
        Upgrades.add(AEItems.REDSTONE_CARD, EAESingletons.EX_IMPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.SPEED_CARD, EAESingletons.EX_IMPORT_BUS, 4, "group.ex_io_bus_part");
        Upgrades.add(AEItems.INVERTER_CARD, EAESingletons.EX_IMPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.FUZZY_CARD, EAESingletons.EX_IMPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.ENERGY_CARD, EAESingletons.WIRELESS_CONNECTOR, 4);
        Upgrades.add(AEItems.ENERGY_CARD, EAESingletons.WIRELESS_HUB, 4);
        Upgrades.add(AEItems.SPEED_CARD, EAESingletons.EX_ASSEMBLER, 5);
        Upgrades.add(AEItems.INVERTER_CARD, EAESingletons.TAG_STORAGE_BUS, 1);
        Upgrades.add(AEItems.VOID_CARD, EAESingletons.TAG_STORAGE_BUS, 1);
        Upgrades.add(AEItems.REDSTONE_CARD, EAESingletons.TAG_EXPORT_BUS, 1);
        Upgrades.add(AEItems.SPEED_CARD, EAESingletons.TAG_EXPORT_BUS, 4);
        Upgrades.add(AEItems.FUZZY_CARD, EAESingletons.THRESHOLD_LEVEL_EMITTER, 1);
        Upgrades.add(AEItems.INVERTER_CARD, EAESingletons.MOD_STORAGE_BUS, 1);
        Upgrades.add(AEItems.VOID_CARD, EAESingletons.MOD_STORAGE_BUS, 1);
        Upgrades.add(AEItems.REDSTONE_CARD, EAESingletons.MOD_EXPORT_BUS, 1);
        Upgrades.add(AEItems.SPEED_CARD, EAESingletons.MOD_EXPORT_BUS, 4);
        Upgrades.add(AEItems.FUZZY_CARD, EAESingletons.ACTIVE_FORMATION_PLANE, 1);
        Upgrades.add(AEItems.CRAFTING_CARD, EAESingletons.ACTIVE_FORMATION_PLANE, 1);
        Upgrades.add(AEItems.REDSTONE_CARD, EAESingletons.ACTIVE_FORMATION_PLANE, 1);
        Upgrades.add(AEItems.INVERTER_CARD, EAESingletons.ACTIVE_FORMATION_PLANE, 1);
        Upgrades.add(AEItems.SPEED_CARD, EAESingletons.ACTIVE_FORMATION_PLANE, 4);
        Upgrades.add(AEItems.CAPACITY_CARD, EAESingletons.ACTIVE_FORMATION_PLANE, 5);
        Upgrades.add(AEItems.CAPACITY_CARD, EAESingletons.PRECISE_EXPORT_BUS, 5);
        Upgrades.add(AEItems.REDSTONE_CARD, EAESingletons.PRECISE_EXPORT_BUS, 1);
        Upgrades.add(AEItems.CRAFTING_CARD, EAESingletons.PRECISE_EXPORT_BUS, 1);
        Upgrades.add(AEItems.SPEED_CARD, EAESingletons.PRECISE_EXPORT_BUS, 4);
        Upgrades.add(AEItems.SPEED_CARD, EAESingletons.EX_IO_PORT, 5);
        Upgrades.add(AEItems.REDSTONE_CARD, EAESingletons.EX_IO_PORT, 1);
        Upgrades.add(AEItems.CAPACITY_CARD, EAESingletons.PRECISE_STORAGE_BUS, 5);
        Upgrades.add(AEItems.VOID_CARD, EAESingletons.PRECISE_STORAGE_BUS, 1);
        Upgrades.add(AEItems.CAPACITY_CARD, EAESingletons.THRESHOLD_EXPORT_BUS, 5);
        Upgrades.add(AEItems.REDSTONE_CARD, EAESingletons.THRESHOLD_EXPORT_BUS, 1);
        Upgrades.add(AEItems.SPEED_CARD, EAESingletons.THRESHOLD_EXPORT_BUS, 4);
        Upgrades.add(AEItems.SPEED_CARD, EAESingletons.CRYSTAL_ASSEMBLER, 4);
        Upgrades.add(AEItems.SPEED_CARD, EAESingletons.CIRCUIT_CUTTER, 4);
        Upgrades.add(AEItems.FUZZY_CARD, EAESingletons.VOID_CELL, 1);
        Upgrades.add(AEItems.INVERTER_CARD, EAESingletons.VOID_CELL, 1);
        Upgrades.add(AEItems.FUZZY_CARD, EAESingletons.SMART_ANNIHILATION_PLANE, 1);
        Upgrades.add(AEItems.INVERTER_CARD, EAESingletons.SMART_ANNIHILATION_PLANE, 1);
        Upgrades.add(AEItems.CAPACITY_CARD, EAESingletons.SMART_ANNIHILATION_PLANE, 5);
    }

    private void registerStorageHandler() {
        StorageCells.addCellHandler(InfinityCellInventory.HANDLER);
        StorageCells.addCellHandler(VoidCellInventory.HANDLER);
        StorageCellModels.registerModel(EAESingletons.INFINITY_WATER_CELL, ExtendedAE.id("block/drive/infinity_water_cell"));
        StorageCellModels.registerModel(EAESingletons.INFINITY_COBBLESTONE_CELL, ExtendedAE.id("block/drive/infinity_cobblestone_cell"));
        StorageCellModels.registerModel(EAESingletons.VOID_CELL, ExtendedAE.id("block/drive/void_cell"));
    }

    private void initPackageList() {
        EAEConfig.tapeWhitelist.forEach(ItemMEPackingTape::registerPackableDevice);
    }

    private void registerRandomAPI() {
        PatternGuiHandler.addPatternHandler(AEProcessingPattern.class, ContainerProcessingPattern.ID);
        PatternGuiHandler.addPatternHandler(AECraftingPattern.class, ContainerCraftingPattern.ID);
        PatternGuiHandler.addPatternHandler(AEStonecuttingPattern.class, ContainerStonecuttingPattern.ID);
        PatternGuiHandler.addPatternHandler(AESmithingTablePattern.class, ContainerSmithingTablePattern.ID);
    }

    public void registerTab(Registry<@NotNull CreativeModeTab> registry) {
        var tab = CreativeModeTab.builder()
                .icon(() -> new ItemStack(EAESingletons.EX_PATTERN_PROVIDER))
                .title(Component.translatable("itemGroup.extendedae"))
                .displayItems((p, o) -> {
                    for (var entry : this.items.getEntries()) {
                        if (entry.value() instanceof AEBaseItem aeItem) {
                            aeItem.addToMainCreativeTab(p, o);
                        } else {
                            o.accept(entry.value());
                        }
                    }
                    for (var entry : this.blocks.getEntries()) {
                        o.accept(entry.value());
                    }
                })
                .build();
        Registry.register(registry, ExtendedAE.id("tab_main"), tab);
    }

    public interface TileFactory<T extends BlockEntity> {

        T create(BlockEntityType<@NotNull T> type, BlockPos worldPosition, BlockState blockState);

    }

}
