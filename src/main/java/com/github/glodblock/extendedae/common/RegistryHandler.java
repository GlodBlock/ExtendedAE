package com.github.glodblock.extendedae.common;

import appeng.api.behaviors.GenericInternalInventory;
import appeng.api.client.StorageCellModels;
import appeng.api.implementations.blockentities.ICraftingMachine;
import appeng.api.implementations.blockentities.ICrankable;
import appeng.api.inventories.PartApiLookup;
import appeng.api.parts.PartModels;
import appeng.api.storage.MEStorage;
import appeng.api.storage.StorageCells;
import appeng.block.AEBaseBlockItem;
import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.ClientTickingBlockEntity;
import appeng.blockentity.ServerTickingBlockEntity;
import appeng.items.AEBaseItem;
import com.github.glodblock.extendedae.EAE;
import com.github.glodblock.extendedae.common.inventory.InfinityCellInventory;
import com.github.glodblock.extendedae.common.items.ItemMEPackingTape;
import com.github.glodblock.extendedae.common.parts.PartExExportBus;
import com.github.glodblock.extendedae.common.parts.PartExImportBus;
import com.github.glodblock.extendedae.common.parts.PartExInterface;
import com.github.glodblock.extendedae.common.parts.PartExPatternAccessTerminal;
import com.github.glodblock.extendedae.common.parts.PartExPatternProvider;
import com.github.glodblock.extendedae.common.tileentities.TileExCharger;
import com.github.glodblock.extendedae.common.tileentities.TileExInscriber;
import com.github.glodblock.extendedae.common.tileentities.TileExInterface;
import com.github.glodblock.extendedae.common.tileentities.TileExMolecularAssembler;
import com.github.glodblock.extendedae.common.tileentities.TileExPatternProvider;
import com.github.glodblock.extendedae.common.tileentities.TileIngredientBuffer;
import com.github.glodblock.extendedae.config.EPPConfig;
import com.github.glodblock.extendedae.container.ContainerExDrive;
import com.github.glodblock.extendedae.container.ContainerExIOBus;
import com.github.glodblock.extendedae.container.ContainerExInscriber;
import com.github.glodblock.extendedae.container.ContainerExInterface;
import com.github.glodblock.extendedae.container.ContainerExMolecularAssembler;
import com.github.glodblock.extendedae.container.ContainerExPatternProvider;
import com.github.glodblock.extendedae.container.ContainerExPatternTerminal;
import com.github.glodblock.extendedae.container.ContainerIngredientBuffer;
import com.github.glodblock.extendedae.container.ContainerPatternModifier;
import com.github.glodblock.extendedae.container.ContainerWirelessConnector;
import com.github.glodblock.extendedae.container.pattern.ContainerCraftingPattern;
import com.github.glodblock.extendedae.container.pattern.ContainerProcessingPattern;
import com.github.glodblock.extendedae.container.pattern.ContainerSmithingTablePattern;
import com.github.glodblock.extendedae.container.pattern.ContainerStonecuttingPattern;
import com.github.glodblock.extendedae.util.FCUtil;
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
        var wake = new MenuType[] {
                ContainerExPatternProvider.TYPE,
                ContainerExInterface.TYPE,
                ContainerExIOBus.EXPORT_TYPE,
                ContainerExPatternTerminal.TYPE,
                ContainerWirelessConnector.TYPE,
                ContainerIngredientBuffer.TYPE,
                ContainerExDrive.TYPE,
                ContainerPatternModifier.TYPE,
                ContainerExMolecularAssembler.TYPE,
                ContainerExInscriber.TYPE
        };
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
        this.initPackageList();
        this.bindItemTab();
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
