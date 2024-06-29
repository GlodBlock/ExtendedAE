package com.glodblock.github.ae2netanalyser.network;

import com.glodblock.github.ae2netanalyser.AEAnalyser;
import com.glodblock.github.ae2netanalyser.network.packets.CAnalyserConfigSave;
import com.glodblock.github.ae2netanalyser.network.packets.CAnalyserGeneric;
import com.glodblock.github.ae2netanalyser.network.packets.SAnalyserConfigInit;
import com.glodblock.github.ae2netanalyser.network.packets.SNetworkDataUpdate;
import com.glodblock.github.glodium.network.NetworkHandler;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public class AEANetworkHandler extends NetworkHandler {

    public static final AEANetworkHandler INSTANCE = new AEANetworkHandler();

    public AEANetworkHandler() {
        super(AEAnalyser.MODID);
        registerPacket(SNetworkDataUpdate::new);
        registerPacket(CAnalyserConfigSave::new);
        registerPacket(SAnalyserConfigInit::new);
        registerPacket(CAnalyserGeneric::new);
    }

    public void onRegister(RegisterPayloadHandlersEvent event) {
        super.onRegister(event);
    }

}
