package com.glodblock.github.appflux.client;

import appeng.api.client.AEKeyRendering;
import appeng.items.storage.BasicStorageCell;
import com.glodblock.github.appflux.client.render.FluxKeyRenderHandler;
import com.glodblock.github.appflux.common.AFSingletons;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import com.glodblock.github.appflux.common.me.key.type.FluxKeyType;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.util.FastColor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

public class AFClientRegistryHandler {

    public static final AFClientRegistryHandler INSTANCE = new AFClientRegistryHandler();

    @SubscribeEvent
    public void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(makeOpaque(BasicStorageCell::getColor), AFSingletons.FE_CELL_1k);
        event.register(makeOpaque(BasicStorageCell::getColor), AFSingletons.FE_CELL_4k);
        event.register(makeOpaque(BasicStorageCell::getColor), AFSingletons.FE_CELL_16k);
        event.register(makeOpaque(BasicStorageCell::getColor), AFSingletons.FE_CELL_64k);
        event.register(makeOpaque(BasicStorageCell::getColor), AFSingletons.FE_CELL_256k);
        event.register(makeOpaque(BasicStorageCell::getColor), AFSingletons.FE_CELL_1M);
        event.register(makeOpaque(BasicStorageCell::getColor), AFSingletons.FE_CELL_4M);
        event.register(makeOpaque(BasicStorageCell::getColor), AFSingletons.FE_CELL_16M);
        event.register(makeOpaque(BasicStorageCell::getColor), AFSingletons.FE_CELL_64M);
        event.register(makeOpaque(BasicStorageCell::getColor), AFSingletons.FE_CELL_256M);
    }

    public void init() {
        AEKeyRendering.register(FluxKeyType.TYPE, FluxKey.class, FluxKeyRenderHandler.INSTANCE);
    }

    private static ItemColor makeOpaque(ItemColor itemColor) {
        return (stack, tintIndex) -> FastColor.ARGB32.opaque(itemColor.getColor(stack, tintIndex));
    }

}
