package com.glodblock.github.appflux.network;

import com.glodblock.github.appflux.AppFlux;
import com.glodblock.github.glodium.network.NetworkHandler;

public class AFNetworkHandler extends NetworkHandler {

    public static final AFNetworkHandler INSTANCE = new AFNetworkHandler();

    public AFNetworkHandler() {
        super(AppFlux.MODID);
        registerPacket(SAFGenericPacket::new);
        registerPacket(CAFGenericPacket::new);
    }

}
