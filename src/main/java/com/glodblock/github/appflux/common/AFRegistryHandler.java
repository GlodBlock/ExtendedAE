package com.glodblock.github.appflux.common;

import appeng.api.AECapabilities;
import appeng.api.behaviors.ContainerItemStrategy;
import appeng.api.behaviors.GenericSlotCapacities;
import appeng.api.client.StorageCellModels;
import appeng.api.networking.IInWorldGridNodeHost;
import appeng.api.parts.PartModels;
import appeng.api.parts.RegisterPartCapabilitiesEvent;
import appeng.api.stacks.AEKeyTypes;
import appeng.api.storage.StorageCells;
import appeng.api.upgrades.Upgrades;
import appeng.block.AEBaseBlockItem;
import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.ClientTickingBlockEntity;
import appeng.blockentity.ServerTickingBlockEntity;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.AEParts;
import appeng.core.localization.GuiText;
import appeng.items.AEBaseItem;
import appeng.parts.automation.StackWorldBehaviors;
import com.glodblock.github.appflux.AppFlux;
import com.glodblock.github.appflux.api.IFluxCell;
import com.glodblock.github.appflux.common.me.cell.FECellHandler;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import com.glodblock.github.appflux.common.me.key.type.FluxKeyType;
import com.glodblock.github.appflux.common.me.strategy.FEContainerItemStrategy;
import com.glodblock.github.appflux.common.me.strategy.FEExternalStorageStrategy;
import com.glodblock.github.appflux.common.me.strategy.FEStackExportStrategy;
import com.glodblock.github.appflux.common.me.strategy.FEStackImportStrategy;
import com.glodblock.github.appflux.common.parts.PartFluxAccessor;
import com.glodblock.github.appflux.common.tileentities.TileFluxAccessor;
import com.glodblock.github.glodium.registry.RegistryHandler;
import com.glodblock.github.glodium.util.GlodUtil;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.apache.commons.lang3.tuple.Pair;

public class AFRegistryHandler extends RegistryHandler {

    public static final AFRegistryHandler INSTANCE = new AFRegistryHandler();

    public AFRegistryHandler() {
        super(AppFlux.MODID);
        this.cap(IInWorldGridNodeHost.class, AECapabilities.IN_WORLD_GRID_NODE_HOST, (object, context) -> object);
        this.cap(TileFluxAccessor.class, Capabilities.EnergyStorage.BLOCK, (te, side) -> te.getEnergyStorage());
        this.cap(IFluxCell.class, Capabilities.EnergyStorage.ITEM, (cell, v) -> ((IFluxCell) cell.getItem()).getCapability(cell, v));
    }

    public <T extends AEBaseBlockEntity> void block(String name, AEBaseEntityBlock<T> block, Class<T> clazz, BlockEntityType.BlockEntitySupplier<? extends T> supplier) {
        bindTileEntity(clazz, block, supplier);
        block(name, block, b -> new AEBaseBlockItem(b, new Item.Properties()));
        tile(name, block.getBlockEntityType());
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

    @SuppressWarnings("UnstableApiUsage")
    public void init() {
        StackWorldBehaviors.registerExternalStorageStrategy(FluxKeyType.TYPE, FEExternalStorageStrategy::new);
        StackWorldBehaviors.registerExportStrategy(FluxKeyType.TYPE, FEStackExportStrategy::new);
        StackWorldBehaviors.registerImportStrategy(FluxKeyType.TYPE, FEStackImportStrategy::new);
        ContainerItemStrategy.register(FluxKeyType.TYPE, FluxKey.class, new FEContainerItemStrategy());
        GenericSlotCapacities.register(FluxKeyType.TYPE, 1000000L);
        StorageCells.addCellHandler(FECellHandler.HANDLER);
        StorageCellModels.registerModel(AFItemAndBlock.FE_CELL_1k, AppFlux.id("block/drive/fe_cell"));
        StorageCellModels.registerModel(AFItemAndBlock.FE_CELL_4k, AppFlux.id("block/drive/fe_cell"));
        StorageCellModels.registerModel(AFItemAndBlock.FE_CELL_16k, AppFlux.id("block/drive/fe_cell"));
        StorageCellModels.registerModel(AFItemAndBlock.FE_CELL_64k, AppFlux.id("block/drive/fe_cell"));
        StorageCellModels.registerModel(AFItemAndBlock.FE_CELL_256k, AppFlux.id("block/drive/fe_cell"));
        for (Pair<String, Block> entry : blocks) {
            Block block = BuiltInRegistries.BLOCK.get(AppFlux.id(entry.getKey()));
            if (block instanceof AEBaseEntityBlock<?>) {
                AEBaseBlockEntity.registerBlockEntityItem(
                        ((AEBaseEntityBlock<?>) block).getBlockEntityType(),
                        block.asItem()
                );
            }
        }
        Upgrades.add(AEItems.VOID_CARD, AFItemAndBlock.FE_CELL_1k, 1, GuiText.StorageCells.getTranslationKey());
        Upgrades.add(AEItems.VOID_CARD, AFItemAndBlock.FE_CELL_4k, 1, GuiText.StorageCells.getTranslationKey());
        Upgrades.add(AEItems.VOID_CARD, AFItemAndBlock.FE_CELL_16k, 1, GuiText.StorageCells.getTranslationKey());
        Upgrades.add(AEItems.VOID_CARD, AFItemAndBlock.FE_CELL_64k, 1, GuiText.StorageCells.getTranslationKey());
        Upgrades.add(AEItems.VOID_CARD, AFItemAndBlock.FE_CELL_256k, 1, GuiText.StorageCells.getTranslationKey());
        Upgrades.add(AFItemAndBlock.INDUCTION_CARD, AEBlocks.INTERFACE, 1, GuiText.Interface.getTranslationKey());
        Upgrades.add(AFItemAndBlock.INDUCTION_CARD, AEParts.INTERFACE, 1, GuiText.Interface.getTranslationKey());
        Upgrades.add(AFItemAndBlock.INDUCTION_CARD, AEBlocks.PATTERN_PROVIDER, 1, "group.pattern_provider.name");
        Upgrades.add(AFItemAndBlock.INDUCTION_CARD, AEParts.PATTERN_PROVIDER, 1, "group.pattern_provider.name");
    }

    @Override
    public void runRegister() {
        super.runRegister();
        AEKeyTypes.register(FluxKeyType.TYPE);
        PartModels.registerModels(PartFluxAccessor.RL);
    }

    @SubscribeEvent
    @Override
    public void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        super.onRegisterCapabilities(event);
    }

    @SubscribeEvent
    public void registerPartCap(RegisterPartCapabilitiesEvent event) {
        event.register(
                Capabilities.EnergyStorage.BLOCK,
                (part, direction) -> part.getEnergyStorage(),
                PartFluxAccessor.class
        );
    }

    public void registerTab(Registry<CreativeModeTab> registry) {
        var tab = CreativeModeTab.builder()
                .icon(() -> new ItemStack(AFItemAndBlock.FE_CELL_1k))
                .title(Component.translatable("itemGroup.af"))
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
        Registry.register(registry, AppFlux.id("tab_main"), tab);
    }

}
