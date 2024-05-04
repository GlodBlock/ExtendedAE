package com.glodblock.github.ae2netanalyser.network;

import com.glodblock.github.ae2netanalyser.AEAnalyser;
import com.glodblock.github.ae2netanalyser.network.packets.CAnalyserConfigSave;
import com.glodblock.github.ae2netanalyser.network.packets.CAnalyserGeneric;
import com.glodblock.github.ae2netanalyser.network.packets.SAnalyserConfigInit;
import com.glodblock.github.ae2netanalyser.network.packets.SNetworkDataUpdate;
import com.glodblock.github.glodium.network.NetworkHandler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;

public class AEANetworkHandler extends NetworkHandler {

    public static final AEANetworkHandler INSTANCE = new AEANetworkHandler();

    public AEANetworkHandler() {
        super(AEAnalyser.MODID);
        registerPacket(AEAnalyser.id("data_update"), SNetworkDataUpdate::new);
        registerPacket(AEAnalyser.id("config_save"), CAnalyserConfigSave::new);
        registerPacket(AEAnalyser.id("config_init"), SAnalyserConfigInit::new);
        registerPacket(AEAnalyser.id("client_generic"), CAnalyserGeneric::new);
    }

    public void onRegister(RegisterPayloadHandlerEvent event) {
        super.onRegister(event);
    }

}
