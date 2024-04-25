package com.github.glodblock.extendedae.common;

import appeng.api.behaviors.GenericInternalInventory;
import appeng.api.client.StorageCellModels;
import appeng.api.features.GridLinkables;
import appeng.api.implementations.blockentities.ICraftingMachine;
import appeng.api.implementations.blockentities.ICrankable;
import appeng.api.inventories.PartApiLookup;
import appeng.api.parts.PartModels;
import appeng.api.storage.MEStorage;
import appeng.api.storage.StorageCells;
import appeng.api.upgrades.Upgrades;
import appeng.block.AEBaseBlockItem;
import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.ClientTickingBlockEntity;
import appeng.blockentity.ServerTickingBlockEntity;
import appeng.core.AppEng;
import appeng.core.definitions.AEItems;
import appeng.core.localization.GuiText;
import appeng.hotkeys.HotkeyActions;
import appeng.hotkeys.InventoryHotkeyAction;
import appeng.items.AEBaseItem;
import appeng.items.tools.powered.WirelessTerminalItem;
import com.github.glodblock.extendedae.EAE;
import com.github.glodblock.extendedae.common.inventory.InfinityCellInventory;
import com.github.glodblock.extendedae.common.items.ItemMEPackingTape;
import com.github.glodblock.extendedae.common.parts.PartActiveFormationPlane;
import com.github.glodblock.extendedae.common.parts.PartExExportBus;
import com.github.glodblock.extendedae.common.parts.PartExImportBus;
import com.github.glodblock.extendedae.common.parts.PartExInterface;
import com.github.glodblock.extendedae.common.parts.PartExPatternAccessTerminal;
import com.github.glodblock.extendedae.common.parts.PartExPatternProvider;
import com.github.glodblock.extendedae.common.parts.PartModExportBus;
import com.github.glodblock.extendedae.common.parts.PartModStorageBus;
import com.github.glodblock.extendedae.common.parts.PartTagExportBus;
import com.github.glodblock.extendedae.common.parts.PartTagStorageBus;
import com.github.glodblock.extendedae.common.parts.PartThresholdLevelEmitter;
import com.github.glodblock.extendedae.common.tileentities.TileCaner;
import com.github.glodblock.extendedae.common.tileentities.TileExCharger;
import com.github.glodblock.extendedae.common.tileentities.TileExInscriber;
import com.github.glodblock.extendedae.common.tileentities.TileExInterface;
import com.github.glodblock.extendedae.common.tileentities.TileExMolecularAssembler;
import com.github.glodblock.extendedae.common.tileentities.TileExPatternProvider;
import com.github.glodblock.extendedae.common.tileentities.TileIngredientBuffer;
import com.github.glodblock.extendedae.config.EPPConfig;
import com.github.glodblock.extendedae.container.ContainerActiveFormationPlane;
import com.github.glodblock.extendedae.container.ContainerCaner;
import com.github.glodblock.extendedae.container.ContainerExDrive;
import com.github.glodblock.extendedae.container.ContainerExIOBus;
import com.github.glodblock.extendedae.container.ContainerExIOPort;
import com.github.glodblock.extendedae.container.ContainerExInscriber;
import com.github.glodblock.extendedae.container.ContainerExInterface;
import com.github.glodblock.extendedae.container.ContainerExMolecularAssembler;
import com.github.glodblock.extendedae.container.ContainerExPatternProvider;
import com.github.glodblock.extendedae.container.ContainerExPatternTerminal;
import com.github.glodblock.extendedae.container.ContainerIngredientBuffer;
import com.github.glodblock.extendedae.container.ContainerModExportBus;
import com.github.glodblock.extendedae.container.ContainerModStorageBus;
import com.github.glodblock.extendedae.container.ContainerPatternModifier;
import com.github.glodblock.extendedae.container.ContainerRenamer;
import com.github.glodblock.extendedae.container.ContainerTagExportBus;
import com.github.glodblock.extendedae.container.ContainerTagStorageBus;
import com.github.glodblock.extendedae.container.ContainerThresholdLevelEmitter;
import com.github.glodblock.extendedae.container.ContainerWirelessConnector;
import com.github.glodblock.extendedae.container.ContainerWirelessExPAT;
import com.github.glodblock.extendedae.container.pattern.ContainerCraftingPattern;
import com.github.glodblock.extendedae.container.pattern.ContainerProcessingPattern;
import com.github.glodblock.extendedae.container.pattern.ContainerSmithingTablePattern;
import com.github.glodblock.extendedae.container.pattern.ContainerStonecuttingPattern;
import com.github.glodblock.extendedae.util.FCUtil;
import com.github.glodblock.extendedae.xmod.wt.WTCommonLoad;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

import static appeng.api.features.HotkeyAction.WIRELESS_TERMINAL;

public class RegistryHandler {

    public static final RegistryHandler INSTANCE = new RegistryHandler();

    protected final List<Pair<String, Block>> blocks = new ArrayList<>();
    protected final List<Pair<String, Item>> items = new ArrayList<>();
    protected final List<Pair<String, BlockEntityType<?>>> tiles = new ArrayList<>();

    public void block(String name, Block block) {
        blocks.add(Pair.of(name, block));
        if (block instanceof AEBaseEntityBlock<?> tileBlock) {
            tile(name, tileBlock.getBlockEntityType());
        }
    }

    public <T extends AEBaseBlockEntity> void block(String name, AEBaseEntityBlock<T> block, Class<T> clazz, BlockEntityType.BlockEntitySupplier<? extends T> supplier) {
        bindTileEntity(clazz, block, supplier);
        block(name, block);
    }

    public void item(String name, Item item) {
        items.add(Pair.of(name, item));
    }

    public void tile(String name, BlockEntityType<?> type) {
        tiles.add(Pair.of(name, type));
    }

    public void runRegister() {
        this.onRegisterBlocks();
        this.onRegisterItems();
        this.onRegisterTileEntities();
        this.onRegisterContainer();
        this.onRegisterModels();
        this.onRegisterRandomAPI();
        if (EAE.checkMod("ae2wtlib")) {
            WTCommonLoad.init();
        }
    }

    private void onRegisterBlocks() {
        for (Pair<String, Block> entry : blocks) {
            String key = entry.getLeft();
            Block block = entry.getRight();
            Registry.register(BuiltInRegistries.BLOCK, EAE.id(key), block);
        }
    }

    private void onRegisterItems() {
        for (Pair<String, Block> entry : blocks) {
            Registry.register(BuiltInRegistries.ITEM, EAE.id(entry.getLeft()), new AEBaseBlockItem(entry.getRight(), new Item.Properties()));
        }
        for (Pair<String, Item> entry : items) {
            Registry.register(BuiltInRegistries.ITEM, EAE.id(entry.getLeft()), entry.getRight());
        }
    }

    private void onRegisterTileEntities() {
        for (Pair<String, BlockEntityType<?>> entry : tiles) {
            String key = entry.getLeft();
            BlockEntityType<?> tile = entry.getRight();
            Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, EAE.id(key), tile);
        }
    }

    private void onRegisterContainer() {
        Registry.register(BuiltInRegistries.MENU, ContainerProcessingPattern.ID, ContainerProcessingPattern.TYPE);
        Registry.register(BuiltInRegistries.MENU, ContainerCraftingPattern.ID, ContainerCraftingPattern.TYPE);
        Registry.register(BuiltInRegistries.MENU, ContainerStonecuttingPattern.ID, ContainerStonecuttingPattern.TYPE);
        Registry.register(BuiltInRegistries.MENU, ContainerSmithingTablePattern.ID, ContainerSmithingTablePattern.TYPE);
        registerMenuType("ex_pattern_provider", ContainerExPatternProvider.TYPE);
        registerMenuType("ex_interface", ContainerExInterface.TYPE);
        registerMenuType("ex_export_bus", ContainerExIOBus.EXPORT_TYPE);
        registerMenuType("ex_import_bus", ContainerExIOBus.IMPORT_TYPE);
        registerMenuType("ex_pattern_access_terminal", ContainerExPatternTerminal.TYPE);
        registerMenuType("wireless_connector", ContainerWirelessConnector.TYPE);
        registerMenuType("ingredient_buffer", ContainerIngredientBuffer.TYPE);
        registerMenuType("ex_drive", ContainerExDrive.TYPE);
        registerMenuType("pattern_modifier", ContainerPatternModifier.TYPE);
        registerMenuType("ex_molecular_assembler", ContainerExMolecularAssembler.TYPE);
        registerMenuType("ex_inscriber", ContainerExInscriber.TYPE);
        registerMenuType("tag_storage_bus", ContainerTagStorageBus.TYPE);
        registerMenuType("tag_export_bus", ContainerTagExportBus.TYPE);
        registerMenuType("threshold_level_emitter", ContainerThresholdLevelEmitter.TYPE);
        registerMenuType("renamer", ContainerRenamer.TYPE);
        registerMenuType("mod_storage_bus", ContainerModStorageBus.TYPE);
        registerMenuType("mod_export_bus", ContainerModExportBus.TYPE);
        registerMenuType("active_formation_plane", ContainerActiveFormationPlane.TYPE);
        registerMenuType("caner", ContainerCaner.TYPE);
        registerMenuType("wireless_ex_pat", ContainerWirelessExPAT.TYPE);
        registerMenuType("ex_ioport", ContainerExIOPort.TYPE);
        if (EAE.checkMod("ae2wtlib")) {
            WTCommonLoad.container();
        }
    }
    
    private void registerMenuType(String id, MenuType<?> menuType) {
        if (!BuiltInRegistries.MENU.containsKey(AppEng.makeId(id))) {
            Registry.register(BuiltInRegistries.MENU, AppEng.makeId(id), menuType);
        }
    }

    private <T extends AEBaseBlockEntity> void bindTileEntity(Class<T> clazz, AEBaseEntityBlock<T> block, BlockEntityType.BlockEntitySupplier<? extends T> supplier) {
        BlockEntityTicker<T> serverTicker = null;
        if (ServerTickingBlockEntity.class.isAssignableFrom(clazz)) {
            serverTicker = (level, pos, state, entity) -> ((ServerTickingBlockEntity) entity).serverTick();
        }
        BlockEntityTicker<T> clientTicker = null;
        if (ClientTickingBlockEntity.class.isAssignableFrom(clazz)) {
            clientTicker = (level, pos, state, entity) -> ((ClientTickingBlockEntity) entity).clientTick();
        }
        block.setBlockEntity(clazz, FCUtil.getTileType(clazz, supplier, block), clientTicker, serverTicker);
    }

    public void onInit() {
        for (Pair<String, Block> entry : blocks) {
            Block block = BuiltInRegistries.BLOCK.get(EAE.id(entry.getKey()));
            if (block instanceof AEBaseEntityBlock<?>) {
                AEBaseBlockEntity.registerBlockEntityItem(
                        ((AEBaseEntityBlock<?>) block).getBlockEntityType(),
                        block.asItem()
                );
            }
        }
        this.registerStorageHandler();
        this.registerCapabilities();
        this.registerUpgrade();
        this.initPackageList();
        this.bindItemTab();
    }

    private void registerUpgrade() {
        Upgrades.add(AEItems.FUZZY_CARD, EAEItemAndBlock.EX_INTERFACE.asItem(), 1, "gui.extendedae.ex_interface");
        Upgrades.add(AEItems.CRAFTING_CARD, EAEItemAndBlock.EX_INTERFACE.asItem(), 1, "gui.extendedae.ex_interface");
        Upgrades.add(AEItems.FUZZY_CARD, EAEItemAndBlock.EX_INTERFACE_PART.asItem(), 1, "gui.extendedae.ex_interface");
        Upgrades.add(AEItems.CRAFTING_CARD, EAEItemAndBlock.EX_INTERFACE_PART.asItem(), 1, "gui.extendedae.ex_interface");
        Upgrades.add(AEItems.CAPACITY_CARD, EAEItemAndBlock.EX_EXPORT_BUS, 5, "group.ex_io_bus_part");
        Upgrades.add(AEItems.REDSTONE_CARD, EAEItemAndBlock.EX_EXPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.SPEED_CARD, EAEItemAndBlock.EX_EXPORT_BUS, 4, "group.ex_io_bus_part");
        Upgrades.add(AEItems.INVERTER_CARD, EAEItemAndBlock.EX_EXPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.FUZZY_CARD, EAEItemAndBlock.EX_EXPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.CRAFTING_CARD, EAEItemAndBlock.EX_EXPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.CAPACITY_CARD, EAEItemAndBlock.EX_IMPORT_BUS, 5, "group.ex_io_bus_part");
        Upgrades.add(AEItems.REDSTONE_CARD, EAEItemAndBlock.EX_IMPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.SPEED_CARD, EAEItemAndBlock.EX_IMPORT_BUS, 4, "group.ex_io_bus_part");
        Upgrades.add(AEItems.INVERTER_CARD, EAEItemAndBlock.EX_IMPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.FUZZY_CARD, EAEItemAndBlock.EX_IMPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.ENERGY_CARD, EAEItemAndBlock.WIRELESS_CONNECTOR, 4, "gui.extendedae.wireless_connect");
        Upgrades.add(AEItems.SPEED_CARD, EAEItemAndBlock.EX_ASSEMBLER, 5, "gui.extendedae.ex_molecular_assembler");
        Upgrades.add(AEItems.SPEED_CARD, EAEItemAndBlock.EX_INSCRIBER, 4, "gui.extendedae.ex_inscriber");
        Upgrades.add(AEItems.INVERTER_CARD, EAEItemAndBlock.TAG_STORAGE_BUS, 1, "item.extendedae.tag_storage_bus");
        Upgrades.add(AEItems.VOID_CARD, EAEItemAndBlock.TAG_STORAGE_BUS, 1, "item.extendedae.tag_storage_bus");
        Upgrades.add(AEItems.REDSTONE_CARD, EAEItemAndBlock.TAG_EXPORT_BUS, 1, "item.extendedae.tag_export_bus");
        Upgrades.add(AEItems.SPEED_CARD, EAEItemAndBlock.TAG_EXPORT_BUS, 4, "item.extendedae.tag_export_bus");
        Upgrades.add(AEItems.FUZZY_CARD, EAEItemAndBlock.THRESHOLD_LEVEL_EMITTER, 1, "item.extendedae.threshold_level_emitter");
        Upgrades.add(AEItems.INVERTER_CARD, EAEItemAndBlock.MOD_STORAGE_BUS, 1, "item.extendedae.mod_storage_bus");
        Upgrades.add(AEItems.VOID_CARD, EAEItemAndBlock.MOD_STORAGE_BUS, 1, "item.extendedae.mod_storage_bus");
        Upgrades.add(AEItems.REDSTONE_CARD, EAEItemAndBlock.MOD_EXPORT_BUS, 1, "item.extendedae.mod_export_bus");
        Upgrades.add(AEItems.SPEED_CARD, EAEItemAndBlock.MOD_EXPORT_BUS, 4, "item.extendedae.mod_export_bus");
        Upgrades.add(AEItems.FUZZY_CARD, EAEItemAndBlock.ACTIVE_FORMATION_PLANE, 1, "item.extendedae.active_formation_plane");
        Upgrades.add(AEItems.CAPACITY_CARD, EAEItemAndBlock.ACTIVE_FORMATION_PLANE, 5, "item.extendedae.active_formation_plane");
        Upgrades.add(AEItems.ENERGY_CARD, EAEItemAndBlock.WIRELESS_EX_PAT, 2, GuiText.WirelessTerminals.getTranslationKey());
        Upgrades.add(AEItems.SPEED_CARD, EAEItemAndBlock.EX_IO_PORT, 5, "block.extendedae.ex_io_port");
    }

    @SuppressWarnings("all")
    private void registerCapabilities() {
        PartApiLookup.register(
                GenericInternalInventory.SIDED, (part, context) -> part.getInterfaceLogic().getStorage(),
                PartExInterface.class
        );
        PartApiLookup.register(
                MEStorage.SIDED, (part, context) -> part.getInterfaceLogic().getInventory(),
                PartExInterface.class
        );
        PartApiLookup.register(
                GenericInternalInventory.SIDED, (part, context) -> part.getLogic().getReturnInv(),
                PartExPatternProvider.class
        );
        ICrankable.LOOKUP.registerForBlockEntity(TileExCharger::getCrankable, FCUtil.getTileType(TileExCharger.class));
        ICrankable.LOOKUP.registerForBlockEntity(TileExInscriber::getCrankable, FCUtil.getTileType(TileExInscriber.class));
        GenericInternalInventory.SIDED.registerForBlockEntity(
                (blockEntity, context) -> blockEntity.getInterfaceLogic().getStorage(),
                FCUtil.getTileType(TileExInterface.class)
        );
        MEStorage.SIDED.registerForBlockEntity(
                (blockEntity, context) -> blockEntity.getInterfaceLogic().getInventory(),
                FCUtil.getTileType(TileExInterface.class)
        );
        ICraftingMachine.SIDED.registerSelf(FCUtil.getTileType(TileExMolecularAssembler.class));
        GenericInternalInventory.SIDED.registerForBlockEntity(
                (blockEntity, context) -> blockEntity.getLogic().getReturnInv(),
                FCUtil.getTileType(TileExPatternProvider.class)
        );
        GenericInternalInventory.SIDED.registerForBlockEntity(
                (blockEntity, context) -> blockEntity.getInventory(),
                FCUtil.getTileType(TileIngredientBuffer.class)
        );
        ICraftingMachine.SIDED.registerForBlockEntity(
                (blockEntity, context) -> blockEntity,
                FCUtil.getTileType(TileCaner.class)
        );
        GenericInternalInventory.SIDED.registerForBlockEntity(
                (blockEntity, context) -> blockEntity.getStuff(),
                FCUtil.getTileType(TileCaner.class)
        );
    }

    private void registerStorageHandler() {
        StorageCells.addCellHandler(InfinityCellInventory.HANDLER);
        StorageCellModels.registerModel(EAEItemAndBlock.INFINITY_CELL, EAE.id("block/drive/infinity_cell"));
    }

    private void onRegisterModels() {
        PartModels.registerModels(PartExPatternProvider.MODELS);
        PartModels.registerModels(PartExInterface.MODELS);
        PartModels.registerModels(PartExExportBus.MODELS);
        PartModels.registerModels(PartExImportBus.MODELS);
        PartModels.registerModels(PartExPatternAccessTerminal.MODELS);
        PartModels.registerModels(PartTagStorageBus.MODEL_BASE);
        PartModels.registerModels(PartTagExportBus.MODEL_BASE);
        PartModels.registerModels(PartThresholdLevelEmitter.MODELS);
        PartModels.registerModels(PartModStorageBus.MODEL_BASE);
        PartModels.registerModels(PartModExportBus.MODEL_BASE);
        PartModels.registerModels(PartActiveFormationPlane.MODELS);
    }

    private void onRegisterRandomAPI() {
        GridLinkables.register(EAEItemAndBlock.WIRELESS_EX_PAT, WirelessTerminalItem.LINKABLE_HANDLER);
        if (!EAE.checkMod("ae2wtlib")) {
            HotkeyActions.register(new InventoryHotkeyAction(EAEItemAndBlock.WIRELESS_EX_PAT, (player, i) -> EAEItemAndBlock.WIRELESS_EX_PAT.openFromInventory(player, i)), WIRELESS_TERMINAL);
        } else {
            HotkeyActions.register(new InventoryHotkeyAction(EAEItemAndBlock.WIRELESS_EX_PAT, (player, i) -> EAEItemAndBlock.WIRELESS_EX_PAT.openFromInventory(player, i)), "wireless_pattern_access_terminal");
        }
    }

    private void bindItemTab() {
        var tab = FabricItemGroup.builder()
                .icon(() -> new ItemStack(EAEItemAndBlock.EX_PATTERN_PROVIDER))
                .title(Component.translatable("itemGroup.epp"))
                .displayItems((__, o) -> {
                    for (Pair<String, Item> entry : items) {
                        if (entry.getRight() instanceof AEBaseItem aeItem) {
                            aeItem.addToMainCreativeTab(o);
                        } else {
                            o.accept(entry.getRight());
                        }
                    }
                    for (Pair<String, Block> entry : blocks) {
                        o.accept(entry.getRight());
                    }
                })
                .build();
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, EAE.id("tab_main"), tab);
    }

    private void initPackageList() {
        for (var s : EPPConfig.INSTANCE.tapeWhitelist) {
            ItemMEPackingTape.registerPackableDevice(new ResourceLocation(s));
        }
    }

}
