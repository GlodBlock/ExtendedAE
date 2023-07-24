package com.glodblock.github.epp.common;

import appeng.api.behaviors.GenericInternalInventory;
import appeng.api.inventories.PartApiLookup;
import appeng.block.AEBaseBlockItem;
import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.ClientTickingBlockEntity;
import appeng.blockentity.ServerTickingBlockEntity;
import com.glodblock.github.epp.EPP;
import com.glodblock.github.epp.common.parts.PartExPatternProvider;
import com.glodblock.github.epp.common.tiles.TileExPatternProvider;
import com.glodblock.github.epp.container.ContainerExPatternProvider;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.registry.Registry;
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

    public <T extends AEBaseBlockEntity> void block(String name, AEBaseEntityBlock<T> block, Class<T> clazz, BlockEntityType<T> type) {
        bindTileEntity(clazz, block, type);
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
    }

    private void onRegisterBlocks() {
        for (Pair<String, Block> entry : blocks) {
            String key = entry.getLeft();
            Block block = entry.getRight();
            Registry.register(Registry.BLOCK, EPP.id(key), block);
        }
    }

    private void onRegisterItems() {
        for (Pair<String, Block> entry : blocks) {
            Registry.register(Registry.ITEM, EPP.id(entry.getLeft()), new AEBaseBlockItem(entry.getRight(), new Item.Settings().group(EPPItemAndBlock.TAB)));
        }
        for (Pair<String, Item> entry : items) {
            Registry.register(Registry.ITEM, EPP.id(entry.getLeft()), entry.getRight());
        }
    }

    private void onRegisterTileEntities() {
        for (Pair<String, BlockEntityType<?>> entry : tiles) {
            String key = entry.getLeft();
            BlockEntityType<?> tile = entry.getRight();
            Registry.register(Registry.BLOCK_ENTITY_TYPE, EPP.id(key), tile);
        }
    }

    private void onRegisterContainer() {
        ScreenHandlerType<ContainerExPatternProvider> type = ContainerExPatternProvider.TYPE;
    }

    private <T extends AEBaseBlockEntity> void bindTileEntity(Class<T> clazz, AEBaseEntityBlock<T> block, BlockEntityType<T> type) {
        BlockEntityTicker<T> serverTicker = null;
        if (ServerTickingBlockEntity.class.isAssignableFrom(clazz)) {
            serverTicker = (level, pos, state, entity) -> ((ServerTickingBlockEntity) entity).serverTick();
        }
        BlockEntityTicker<T> clientTicker = null;
        if (ClientTickingBlockEntity.class.isAssignableFrom(clazz)) {
            clientTicker = (level, pos, state, entity) -> ((ClientTickingBlockEntity) entity).clientTick();
        }
        block.setBlockEntity(clazz, type, clientTicker, serverTicker);
    }

    @SuppressWarnings("all")
    public void onInit() {
        for (Pair<String, Block> entry : blocks) {
            Block block = Registry.BLOCK.get(EPP.id(entry.getKey()));
            if (block instanceof AEBaseEntityBlock<?>) {
                AEBaseBlockEntity.registerBlockEntityItem(
                        ((AEBaseEntityBlock<?>) block).getBlockEntityType(),
                        block.asItem()
                );
            }
        }
        GenericInternalInventory.SIDED.registerForBlockEntity(
                (blockEntity, context) -> blockEntity.getLogic().getReturnInv(),
                TileExPatternProvider.TYPE
        );
        PartApiLookup.register(
                GenericInternalInventory.SIDED, (part, context) -> part.getLogic().getReturnInv(),
                PartExPatternProvider.class
        );
    }

}
