package com.glodblock.github.extendedae.common;

import appeng.api.client.StorageCellModels;
import appeng.api.parts.PartModels;
import appeng.api.storage.StorageCells;
import appeng.api.upgrades.Upgrades;
import appeng.block.AEBaseBlockItem;
import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.ClientTickingBlockEntity;
import appeng.blockentity.ServerTickingBlockEntity;
import appeng.core.AppEng;
import appeng.core.definitions.AEItems;
import appeng.items.AEBaseItem;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.inventory.InfinityCellInventory;
import com.glodblock.github.extendedae.common.items.ItemMEPackingTape;
import com.glodblock.github.extendedae.common.parts.*;
import com.glodblock.github.extendedae.config.EPPConfig;
import com.glodblock.github.extendedae.container.*;
import com.glodblock.github.extendedae.container.pattern.ContainerCraftingPattern;
import com.glodblock.github.extendedae.container.pattern.ContainerProcessingPattern;
import com.glodblock.github.extendedae.container.pattern.ContainerSmithingTablePattern;
import com.glodblock.github.extendedae.container.pattern.ContainerStonecuttingPattern;
import com.glodblock.github.glodium.registry.RegistryHandler;
import com.glodblock.github.glodium.util.GlodUtil;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.apache.commons.lang3.tuple.Pair;

public class EAERegistryHandler extends RegistryHandler {

    public static final EAERegistryHandler INSTANCE = new EAERegistryHandler();

    public EAERegistryHandler() {
        super(ExtendedAE.MODID);
    }

    public <T extends AEBaseBlockEntity> void block(String name, AEBaseEntityBlock<T> block, Class<T> clazz, BlockEntityType.BlockEntitySupplier<? extends T> supplier) {
        bindTileEntity(clazz, block, supplier);
        block(name, block, b -> new AEBaseBlockItem(b, new Item.Properties()));
        tile(name, block.getBlockEntityType());
    }

    @Override
    public void register(RegisterEvent event) {
        super.register(event);
        this.onRegisterContainer();
        this.onRegisterModels();
    }

    private void onRegisterContainer() {
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("ex_pattern_provider"), ContainerExPatternProvider.TYPE);
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("ex_interface"), ContainerExInterface.TYPE);
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("ex_export_bus"), ContainerExIOBus.EXPORT_TYPE);
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("ex_import_bus"), ContainerExIOBus.IMPORT_TYPE);
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("ex_pattern_access_terminal"), ContainerExPatternTerminal.TYPE);
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("wireless_connector"), ContainerWirelessConnector.TYPE);
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("ingredient_buffer"), ContainerIngredientBuffer.TYPE);
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("ex_drive"), ContainerExDrive.TYPE);
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("pattern_modifier"), ContainerPatternModifier.TYPE);
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("ex_molecular_assembler"), ContainerExMolecularAssembler.TYPE);
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("ex_inscriber"), ContainerExInscriber.TYPE);
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("tag_storage_bus"), ContainerTagStorageBus.TYPE);
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("tag_export_bus"), ContainerTagExportBus.TYPE);
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("threshold_level_emitter"), ContainerThresholdLevelEmitter.TYPE);
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("renamer"), ContainerRenamer.TYPE);
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("mod_storage_bus"), ContainerModStorageBus.TYPE);
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("mod_export_bus"), ContainerModExportBus.TYPE);
        ForgeRegistries.MENU_TYPES.register(ContainerProcessingPattern.ID, ContainerProcessingPattern.TYPE);
        ForgeRegistries.MENU_TYPES.register(ContainerCraftingPattern.ID, ContainerCraftingPattern.TYPE);
        ForgeRegistries.MENU_TYPES.register(ContainerStonecuttingPattern.ID, ContainerStonecuttingPattern.TYPE);
        ForgeRegistries.MENU_TYPES.register(ContainerSmithingTablePattern.ID, ContainerSmithingTablePattern.TYPE);
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
        block.setBlockEntity(clazz, GlodUtil.getTileType(clazz, supplier, block), clientTicker, serverTicker);
    }

    public void onInit() {
        for (Pair<String, Block> entry : blocks) {
            Block block = ForgeRegistries.BLOCKS.getValue(ExtendedAE.id(entry.getKey()));
            if (block instanceof AEBaseEntityBlock<?>) {
                AEBaseBlockEntity.registerBlockEntityItem(
                        ((AEBaseEntityBlock<?>) block).getBlockEntityType(),
                        block.asItem()
                );
            }
        }
        this.registerAEUpgrade();
        this.registerStorageHandler();
        this.initPackageList();
    }

    private void registerAEUpgrade() {
        Upgrades.add(AEItems.FUZZY_CARD, EPPItemAndBlock.EX_INTERFACE.asItem(), 1, "gui.expatternprovider.ex_interface");
        Upgrades.add(AEItems.CRAFTING_CARD, EPPItemAndBlock.EX_INTERFACE.asItem(), 1, "gui.expatternprovider.ex_interface");
        Upgrades.add(AEItems.FUZZY_CARD, EPPItemAndBlock.EX_INTERFACE_PART.asItem(), 1, "gui.expatternprovider.ex_interface");
        Upgrades.add(AEItems.CRAFTING_CARD, EPPItemAndBlock.EX_INTERFACE_PART.asItem(), 1, "gui.expatternprovider.ex_interface");
        Upgrades.add(AEItems.VOID_CARD, EPPItemAndBlock.INFINITY_CELL, 1, "item.expatternprovider.infinity_cell");
        Upgrades.add(AEItems.CAPACITY_CARD, EPPItemAndBlock.EX_EXPORT_BUS, 5, "group.ex_io_bus_part");
        Upgrades.add(AEItems.REDSTONE_CARD, EPPItemAndBlock.EX_EXPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.SPEED_CARD, EPPItemAndBlock.EX_EXPORT_BUS, 4, "group.ex_io_bus_part");
        Upgrades.add(AEItems.FUZZY_CARD, EPPItemAndBlock.EX_EXPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.CRAFTING_CARD, EPPItemAndBlock.EX_EXPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.CAPACITY_CARD, EPPItemAndBlock.EX_IMPORT_BUS, 5, "group.ex_io_bus_part");
        Upgrades.add(AEItems.REDSTONE_CARD, EPPItemAndBlock.EX_IMPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.SPEED_CARD, EPPItemAndBlock.EX_IMPORT_BUS, 4, "group.ex_io_bus_part");
        Upgrades.add(AEItems.INVERTER_CARD, EPPItemAndBlock.EX_IMPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.FUZZY_CARD, EPPItemAndBlock.EX_IMPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.ENERGY_CARD, EPPItemAndBlock.WIRELESS_CONNECTOR, 4, "gui.expatternprovider.wireless_connect");
        Upgrades.add(AEItems.SPEED_CARD, EPPItemAndBlock.EX_ASSEMBLER, 5, "gui.expatternprovider.ex_molecular_assembler");
        Upgrades.add(AEItems.SPEED_CARD, EPPItemAndBlock.EX_INSCRIBER, 4, "gui.expatternprovider.ex_inscriber");
        Upgrades.add(AEItems.SPEED_CARD, EPPItemAndBlock.EX_INSCRIBER, 4, "gui.expatternprovider.ex_inscriber");
        Upgrades.add(AEItems.INVERTER_CARD, EPPItemAndBlock.TAG_STORAGE_BUS, 1, "item.expatternprovider.tag_storage_bus");
        Upgrades.add(AEItems.VOID_CARD, EPPItemAndBlock.TAG_STORAGE_BUS, 1, "item.expatternprovider.tag_storage_bus");
        Upgrades.add(AEItems.REDSTONE_CARD, EPPItemAndBlock.TAG_EXPORT_BUS, 1, "item.expatternprovider.tag_export_bus");
        Upgrades.add(AEItems.SPEED_CARD, EPPItemAndBlock.TAG_EXPORT_BUS, 4, "item.expatternprovider.tag_export_bus");
        Upgrades.add(AEItems.FUZZY_CARD, EPPItemAndBlock.THRESHOLD_LEVEL_EMITTER, 1, "item.expatternprovider.threshold_level_emitter");
        Upgrades.add(AEItems.INVERTER_CARD, EPPItemAndBlock.MOD_STORAGE_BUS, 1, "item.expatternprovider.mod_storage_bus");
        Upgrades.add(AEItems.VOID_CARD, EPPItemAndBlock.MOD_STORAGE_BUS, 1, "item.expatternprovider.mod_storage_bus");
        Upgrades.add(AEItems.REDSTONE_CARD, EPPItemAndBlock.MOD_EXPORT_BUS, 1, "item.expatternprovider.mod_export_bus");
        Upgrades.add(AEItems.SPEED_CARD, EPPItemAndBlock.MOD_EXPORT_BUS, 4, "item.expatternprovider.mod_export_bus");
    }

    private void registerStorageHandler() {
        StorageCells.addCellHandler(InfinityCellInventory.HANDLER);
        StorageCellModels.registerModel(EPPItemAndBlock.INFINITY_CELL, ExtendedAE.id("block/drive/infinity_cell"));
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
    }

    private void initPackageList() {
        EPPConfig.tapeWhitelist.forEach(ItemMEPackingTape::registerPackableDevice);
    }

    public void registerTab(Registry<CreativeModeTab> registry) {
        var tab = CreativeModeTab.builder()
                .icon(() -> new ItemStack(EPPItemAndBlock.EX_PATTERN_PROVIDER))
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
        Registry.register(registry, ExtendedAE.id("tab_main"), tab);
    }

}
