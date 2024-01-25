package com.glodblock.github.ae2netanalyser.network;

import com.glodblock.github.ae2netanalyser.network.packets.CAnalyserConfigSave;
import com.glodblock.github.ae2netanalyser.network.packets.SAnalyserConfigInit;
import com.glodblock.github.ae2netanalyser.network.packets.SNetworkDataUpdate;
import com.glodblock.github.glodium.network.NetworkHandlerServer;
import com.glodblock.github.glodium.network.packet.CGenericPacket;

public final class AEANetworkHandler {

    public static NetworkHandlerServer INSTANCE;

    private AEANetworkHandler() {
        // NO-OP
    }

    public static void init(NetworkHandlerServer instance) {
        INSTANCE = instance;
        INSTANCE.registerPacket(SNetworkDataUpdate.class, SNetworkDataUpdate::new);
        INSTANCE.registerPacket(CAnalyserConfigSave.class, CAnalyserConfigSave::new);
        INSTANCE.registerPacket(SAnalyserConfigInit.class, SAnalyserConfigInit::new);
        INSTANCE.registerPacket(CGenericPacket.class, CGenericPacket::new);
    }

}
