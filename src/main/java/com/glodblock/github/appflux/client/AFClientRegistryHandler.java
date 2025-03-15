package com.glodblock.github.appflux.client;

import appeng.api.client.AEKeyRendering;
import appeng.client.gui.me.common.MEStorageScreen;
import appeng.init.client.InitScreens;
import appeng.items.storage.BasicStorageCell;
import appeng.menu.me.common.MEStorageMenu;
import com.glodblock.github.appflux.client.render.FluxKeyRenderHandler;
import com.glodblock.github.appflux.common.AFContainers;
import com.glodblock.github.appflux.common.AFItemAndBlock;
import com.glodblock.github.appflux.common.items.ItemPortableFECell;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import com.glodblock.github.appflux.common.me.key.type.FluxKeyType;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class AFClientRegistryHandler {

    public static final AFClientRegistryHandler INSTANCE = new AFClientRegistryHandler();

    @SubscribeEvent
    public void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(BasicStorageCell::getColor, AFItemAndBlock.FE_CELL_1k);
        event.register(BasicStorageCell::getColor, AFItemAndBlock.FE_CELL_4k);
        event.register(BasicStorageCell::getColor, AFItemAndBlock.FE_CELL_16k);
        event.register(BasicStorageCell::getColor, AFItemAndBlock.FE_CELL_64k);
        event.register(BasicStorageCell::getColor, AFItemAndBlock.FE_CELL_256k);
        event.register(BasicStorageCell::getColor, AFItemAndBlock.FE_CELL_1M);
        event.register(BasicStorageCell::getColor, AFItemAndBlock.FE_CELL_4M);
        event.register(BasicStorageCell::getColor, AFItemAndBlock.FE_CELL_16M);
        event.register(BasicStorageCell::getColor, AFItemAndBlock.FE_CELL_64M);
        event.register(BasicStorageCell::getColor, AFItemAndBlock.FE_CELL_256M);
        event.register(ItemPortableFECell::getColor, AFItemAndBlock.FE_PORTABLE_CELL_1k);
        event.register(ItemPortableFECell::getColor, AFItemAndBlock.FE_PORTABLE_CELL_4k);
        event.register(ItemPortableFECell::getColor, AFItemAndBlock.FE_PORTABLE_CELL_16k);
        event.register(ItemPortableFECell::getColor, AFItemAndBlock.FE_PORTABLE_CELL_64k);
        event.register(ItemPortableFECell::getColor, AFItemAndBlock.FE_PORTABLE_CELL_256k);
        event.register(ItemPortableFECell::getColor, AFItemAndBlock.FE_PORTABLE_CELL_1M);
        event.register(ItemPortableFECell::getColor, AFItemAndBlock.FE_PORTABLE_CELL_4M);
        event.register(ItemPortableFECell::getColor, AFItemAndBlock.FE_PORTABLE_CELL_16M);
        event.register(ItemPortableFECell::getColor, AFItemAndBlock.FE_PORTABLE_CELL_64M);
        event.register(ItemPortableFECell::getColor, AFItemAndBlock.FE_PORTABLE_CELL_256M);
    }

    public void init() {
        AEKeyRendering.register(FluxKeyType.TYPE, FluxKey.class, FluxKeyRenderHandler.INSTANCE);
        InitScreens.<MEStorageMenu, MEStorageScreen<MEStorageMenu>>register(
                AFContainers.PORTABLE_FE_CELL_TYPE,
                MEStorageScreen::new,
                "/screens/terminals/portable_fe_cell.json"
        );
    }

}
