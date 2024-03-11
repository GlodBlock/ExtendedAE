package com.glodblock.github.appflux.client;

import appeng.api.client.AEKeyRendering;
import appeng.items.storage.BasicStorageCell;
import com.glodblock.github.appflux.client.render.FluxKeyRenderHandler;
import com.glodblock.github.appflux.common.AFItemAndBlock;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import com.glodblock.github.appflux.common.me.key.type.FluxKeyType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

public class AFClientRegistryHandler {

    public static final AFClientRegistryHandler INSTANCE = new AFClientRegistryHandler();

    @SubscribeEvent
    public void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(BasicStorageCell::getColor, AFItemAndBlock.FE_CELL_1k);
        event.register(BasicStorageCell::getColor, AFItemAndBlock.FE_CELL_4k);
        event.register(BasicStorageCell::getColor, AFItemAndBlock.FE_CELL_16k);
        event.register(BasicStorageCell::getColor, AFItemAndBlock.FE_CELL_64k);
        event.register(BasicStorageCell::getColor, AFItemAndBlock.FE_CELL_256k);
    }

    public void init() {
        AEKeyRendering.register(FluxKeyType.TYPE, FluxKey.class, FluxKeyRenderHandler.INSTANCE);
    }

}
