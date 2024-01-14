package com.glodblock.github.ae2netanalyser.network;

import com.glodblock.github.ae2netanalyser.AEAnalyser;
import com.glodblock.github.ae2netanalyser.network.packets.CAnalyserConfigSave;
import com.glodblock.github.ae2netanalyser.network.packets.SAnalyserConfigInit;
import com.glodblock.github.ae2netanalyser.network.packets.SNetworkDataUpdate;
import com.glodblock.github.glodium.network.NetworkHandler;
import com.glodblock.github.glodium.network.packet.CGenericPacket;

public class AEANetworkHandler extends NetworkHandler {

    public static final AEANetworkHandler INSTANCE = new AEANetworkHandler();

    public AEANetworkHandler() {
        super(AEAnalyser.MODID);
    }

    public void init() {
        registerPacket(SNetworkDataUpdate.class, SNetworkDataUpdate::new);
        registerPacket(CAnalyserConfigSave.class, CAnalyserConfigSave::new);
        registerPacket(SAnalyserConfigInit.class, SAnalyserConfigInit::new);
        registerPacket(CGenericPacket.class, CGenericPacket::new);
    }

}
