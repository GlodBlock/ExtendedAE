package com.glodblock.github.appflux.common;

import appeng.api.AECapabilities;
import appeng.api.behaviors.ContainerItemStrategy;
import appeng.api.behaviors.GenericSlotCapacities;
import appeng.api.networking.GridServices;
import appeng.api.networking.IInWorldGridNodeHost;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.api.parts.RegisterPartCapabilitiesEvent;
import appeng.api.stacks.AEKeyType;
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
import appeng.items.parts.PartItem;
import appeng.parts.automation.StackWorldBehaviors;
import com.glodblock.github.appflux.AppFlux;
import com.glodblock.github.appflux.api.IFluxCell;
import com.glodblock.github.appflux.common.items.ItemPortableFECell;
import com.glodblock.github.appflux.common.me.cell.FECellHandler;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import com.glodblock.github.appflux.common.me.key.type.FluxKeyType;
import com.glodblock.github.appflux.common.me.service.EnergyDistributeService;
import com.glodblock.github.appflux.common.me.strategy.FEContainerItemStrategy;
import com.glodblock.github.appflux.common.me.strategy.FEExternalStorageStrategy;
import com.glodblock.github.appflux.common.me.strategy.FEStackExportStrategy;
import com.glodblock.github.appflux.common.me.strategy.FEStackImportStrategy;
import com.glodblock.github.appflux.common.parts.PartFluxAccessor;
import com.glodblock.github.appflux.common.tileentities.TileFluxAccessor;
import com.glodblock.github.appflux.config.AFConfig;
import com.glodblock.github.appflux.container.ContainerFluxAccessor;
import com.glodblock.github.glodium.registry.RegistryHandler;
import com.glodblock.github.glodium.registry.token.TileToken;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;
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
import net.neoforged.neoforge.registries.RegisterEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class AFRegistryHandler extends RegistryHandler {

    public static AFRegistryHandler INSTANCE;

    public AFRegistryHandler(IEventBus modBus) {
        super(AppFlux.MODID, modBus);
        modBus.addListener(this::onRegisterEvent);
        this.cap(IInWorldGridNodeHost.class, AECapabilities.IN_WORLD_GRID_NODE_HOST, (object, _) -> object);
        this.cap(IFluxCell.class, Capabilities.Energy.ITEM, (cell, access) -> ((IFluxCell) cell.getItem()).getCapability(cell, access));
        this.cap(TileFluxAccessor.class, Capabilities.Energy.BLOCK, (te, _) -> te.getEnergyStorage());
    }

    public <T extends AEBaseBlockEntity, B extends AEBaseEntityBlock<T>> DeferredBlock<@NotNull B> block(String name, Function<BlockBehaviour.Properties, B> builder, Class<T> clazz, TileFactory<@NotNull T> supplier) {
        var aeBlock = block(name, builder, BlockBehaviour.Properties.of(), AEBaseBlockItem::new);
        tile(name, clazz, supplier, aeBlock);
        return aeBlock;
    }

    public <P extends IPart> DeferredItem<@NotNull PartItem<P>> item(String name, Class<P> partClass, Function<IPartItem<P>, P> factory) {
        return this.item(name, properties -> new PartItem<>(properties, partClass, factory));
    }

    public <T extends AEBaseBlockEntity> void tile(String name, Class<T> tileClass, TileFactory<@NotNull T> factory, DeferredBlock<? extends @NotNull AEBaseEntityBlock<T>> block) {
        this.tiles.register(name, () -> {
            AtomicReference<BlockEntityType<@NotNull T>> holder = new AtomicReference<>();
            BlockEntityType<@NotNull T> tileType = new BlockEntityType<>((pos, state) -> factory.create(holder.get(), pos, state), Set.of(block.get()));
            holder.set(tileType);
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

    private void onRegisterEvent(RegisterEvent e) {
        if (e.getRegistry().equals(BuiltInRegistries.MENU)) {
            this.onRegisterContainer();
        } else if (e.getRegistryKey().equals(AEKeyType.REGISTRY_KEY)) {
            AEKeyTypes.register(FluxKeyType.TYPE);
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    public void init() {
        StackWorldBehaviors.registerExternalStorageStrategy(FluxKeyType.TYPE, FEExternalStorageStrategy::new);
        StackWorldBehaviors.registerExportStrategy(FluxKeyType.TYPE, FEStackExportStrategy::new);
        if (AFConfig.allowImport()) {
            StackWorldBehaviors.registerImportStrategy(FluxKeyType.TYPE, FEStackImportStrategy::new);
        }
        ContainerItemStrategy.register(FluxKeyType.TYPE, FluxKey.class, new FEContainerItemStrategy());
        GridServices.register(EnergyDistributeService.class, EnergyDistributeService.class);
        GenericSlotCapacities.register(FluxKeyType.TYPE, 1000000L);
        StorageCells.addCellHandler(FECellHandler.HANDLER);
        for (var entry : this.blocks.getEntries()) {
            Block block = entry.value();
            if (block instanceof AEBaseEntityBlock<?>) {
                AEBaseBlockEntity.registerBlockEntityItem(
                        ((AEBaseEntityBlock<?>) block).getBlockEntityType(),
                        block.asItem()
                );
            }
        }
        registerCellUpgrades(
                AFSingletons.FE_CELL_1k, AFSingletons.FE_CELL_4k, AFSingletons.FE_CELL_16k, AFSingletons.FE_CELL_64k, AFSingletons.FE_CELL_256k,
                AFSingletons.FE_CELL_1M, AFSingletons.FE_CELL_4M, AFSingletons.FE_CELL_16M, AFSingletons.FE_CELL_64M, AFSingletons.FE_CELL_256M
        );
        registerPortableCellUpgrades(
                AFSingletons.FE_PORTABLE_CELL_1k, AFSingletons.FE_PORTABLE_CELL_4k, AFSingletons.FE_PORTABLE_CELL_16k, AFSingletons.FE_PORTABLE_CELL_64k, AFSingletons.FE_PORTABLE_CELL_256k,
                AFSingletons.FE_PORTABLE_CELL_1M, AFSingletons.FE_PORTABLE_CELL_4M, AFSingletons.FE_PORTABLE_CELL_16M, AFSingletons.FE_PORTABLE_CELL_64M, AFSingletons.FE_PORTABLE_CELL_256M
        );
        Upgrades.add(AFSingletons.INDUCTION_CARD, AEBlocks.INTERFACE, 1, GuiText.Interface.getTranslationKey());
        Upgrades.add(AFSingletons.INDUCTION_CARD, AEParts.INTERFACE, 1, GuiText.Interface.getTranslationKey());
        Upgrades.add(AFSingletons.INDUCTION_CARD, AEBlocks.PATTERN_PROVIDER, 1, "group.pattern_provider.name");
        Upgrades.add(AFSingletons.INDUCTION_CARD, AEParts.PATTERN_PROVIDER, 1, "group.pattern_provider.name");
    }

    public Collection<Block> getBlocks() {
        return this.blocks.getEntries().stream().map(DeferredHolder::get).map(b -> (Block) b).toList();
    }

    private static void registerCellUpgrades(ItemLike... cells) {
        for (var cell : cells) {
            Upgrades.add(AEItems.VOID_CARD, cell, 1, GuiText.StorageCells.getTranslationKey());
        }
    }

    private static void registerPortableCellUpgrades(ItemLike... cells) {
        for (var cell : cells) {
            Upgrades.add(AEItems.VOID_CARD, cell, 1, GuiText.PortableCells.getTranslationKey());
            Upgrades.add(AEItems.ENERGY_CARD, cell, 2, GuiText.PortableCells.getTranslationKey());
            Upgrades.add(AFSingletons.INDUCTION_CARD, cell, 1, "group.fe_portable_cells.name");
        }
    }

    private void onRegisterContainer() {
        Registry.register(BuiltInRegistries.MENU, AppFlux.id("flux_accessor"), ContainerFluxAccessor.TYPE);
        Registry.register(BuiltInRegistries.MENU, AppFlux.id("portable_fe_cell"), ItemPortableFECell.FE_CELL_TYPE);
    }

    @SubscribeEvent
    public void registerPartCap(RegisterPartCapabilitiesEvent event) {
        event.register(
                Capabilities.Energy.BLOCK,
                (part, _) -> part.getEnergyStorage(),
                PartFluxAccessor.class
        );
    }

    public void registerTab(Registry<@NotNull CreativeModeTab> registry) {
        var tab = CreativeModeTab.builder()
                .icon(AFSingletons.FE_CELL_1k::toStack)
                .title(Component.translatable("itemGroup.af"))
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
        Registry.register(registry, AppFlux.id("tab_main"), tab);
    }

    public interface TileFactory<T extends BlockEntity> {

        T create(BlockEntityType<@NotNull T> type, BlockPos worldPosition, BlockState blockState);

    }

}
