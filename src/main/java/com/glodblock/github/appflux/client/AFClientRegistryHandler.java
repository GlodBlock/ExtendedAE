package com.glodblock.github.appflux.client;

import appeng.api.client.AEKeyRendering;
import appeng.items.storage.BasicStorageCell;
import com.glodblock.github.appflux.client.render.FluxKeyRenderHandler;
import com.glodblock.github.appflux.common.AFItemAndBlock;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import com.glodblock.github.appflux.common.me.key.type.FluxKeyType;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;

public class AFClientRegistryHandler {

    public static final AFClientRegistryHandler INSTANCE = new AFClientRegistryHandler();

    @SubscribeEvent
    public void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(BasicStorageCell::getColor, AFItemAndBlock.FE_CELL_1k);
        event.register(BasicStorageCell::getColor, AFItemAndBlock.FE_CELL_4k);
        event.register(BasicStorageCell::getColor, AFItemAndBlock.FE_CELL_16k);
        event.register(BasicStorageCell::getColor, AFItemAndBlock.FE_CELL_64k);
        event.register(BasicStorageCell::getColor, AFItemAndBlock.FE_CELL_256k);
        if (ModList.get().isLoaded("gtceu")) {
            event.register(BasicStorageCell::getColor, AFItemAndBlock.GTEU_CELL_1k);
            event.register(BasicStorageCell::getColor, AFItemAndBlock.GTEU_CELL_4k);
            event.register(BasicStorageCell::getColor, AFItemAndBlock.GTEU_CELL_16k);
            event.register(BasicStorageCell::getColor, AFItemAndBlock.GTEU_CELL_64k);
            event.register(BasicStorageCell::getColor, AFItemAndBlock.GTEU_CELL_256k);
        }
    }

    public void init() {
        AEKeyRendering.register(FluxKeyType.TYPE, FluxKey.class, FluxKeyRenderHandler.INSTANCE);
    }

}
