package com.glodblock.github.appflux.client;

import appeng.api.client.StorageCellModels;
import appeng.client.InitScreens;
import appeng.client.api.AEKeyRendering;
import appeng.client.gui.me.common.MEStorageScreen;
import appeng.menu.me.common.MEStorageMenu;
import com.glodblock.github.appflux.AppFlux;
import com.glodblock.github.appflux.client.gui.GuiFluxAccessor;
import com.glodblock.github.appflux.client.render.FluxKeyRenderHandler;
import com.glodblock.github.appflux.common.AFSingletons;
import com.glodblock.github.appflux.common.items.ItemPortableFECell;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import com.glodblock.github.appflux.common.me.key.type.FluxKeyType;
import com.glodblock.github.appflux.container.ContainerFluxAccessor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterBlockStateModels;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class AFClientRegistryHandler {

    public static final AFClientRegistryHandler INSTANCE = new AFClientRegistryHandler();

    @SubscribeEvent
    public void registerGui(RegisterMenuScreensEvent event) {
        InitScreens.register(event, ContainerFluxAccessor.TYPE, GuiFluxAccessor::new, "/screens/flux_accessor.json");
        InitScreens.<MEStorageMenu, MEStorageScreen<MEStorageMenu>>register(event, ItemPortableFECell.FE_CELL_TYPE, MEStorageScreen::new, "/screens/terminals/portable_fe_cell.json");
    }

    @SubscribeEvent
    public void registerModels(RegisterBlockStateModels event) {
        StorageCellModels.registerModel(AFSingletons.FE_CELL_1k, AppFlux.id("block/drive/fe_1k_cell"));
        StorageCellModels.registerModel(AFSingletons.FE_CELL_4k, AppFlux.id("block/drive/fe_4k_cell"));
        StorageCellModels.registerModel(AFSingletons.FE_CELL_16k, AppFlux.id("block/drive/fe_16k_cell"));
        StorageCellModels.registerModel(AFSingletons.FE_CELL_64k, AppFlux.id("block/drive/fe_64k_cell"));
        StorageCellModels.registerModel(AFSingletons.FE_CELL_256k, AppFlux.id("block/drive/fe_256k_cell"));
        StorageCellModels.registerModel(AFSingletons.FE_CELL_1M, AppFlux.id("block/drive/fe_1m_cell"));
        StorageCellModels.registerModel(AFSingletons.FE_CELL_4M, AppFlux.id("block/drive/fe_4m_cell"));
        StorageCellModels.registerModel(AFSingletons.FE_CELL_16M, AppFlux.id("block/drive/fe_16m_cell"));
        StorageCellModels.registerModel(AFSingletons.FE_CELL_64M, AppFlux.id("block/drive/fe_64m_cell"));
        StorageCellModels.registerModel(AFSingletons.FE_CELL_256M, AppFlux.id("block/drive/fe_256m_cell"));
        StorageCellModels.registerModel(AFSingletons.FE_PORTABLE_CELL_1k, AppFlux.id("block/drive/fe_1k_cell"));
        StorageCellModels.registerModel(AFSingletons.FE_PORTABLE_CELL_4k, AppFlux.id("block/drive/fe_4k_cell"));
        StorageCellModels.registerModel(AFSingletons.FE_PORTABLE_CELL_16k, AppFlux.id("block/drive/fe_16k_cell"));
        StorageCellModels.registerModel(AFSingletons.FE_PORTABLE_CELL_64k, AppFlux.id("block/drive/fe_64k_cell"));
        StorageCellModels.registerModel(AFSingletons.FE_PORTABLE_CELL_256k, AppFlux.id("block/drive/fe_256k_cell"));
        StorageCellModels.registerModel(AFSingletons.FE_PORTABLE_CELL_1M, AppFlux.id("block/drive/fe_1m_cell"));
        StorageCellModels.registerModel(AFSingletons.FE_PORTABLE_CELL_4M, AppFlux.id("block/drive/fe_4m_cell"));
        StorageCellModels.registerModel(AFSingletons.FE_PORTABLE_CELL_16M, AppFlux.id("block/drive/fe_16m_cell"));
        StorageCellModels.registerModel(AFSingletons.FE_PORTABLE_CELL_64M, AppFlux.id("block/drive/fe_64m_cell"));
        StorageCellModels.registerModel(AFSingletons.FE_PORTABLE_CELL_256M, AppFlux.id("block/drive/fe_256m_cell"));
    }

    public void init() {
        AEKeyRendering.register(FluxKeyType.TYPE, FluxKey.class, FluxKeyRenderHandler.INSTANCE);
    }

}
