package com.glodblock.github.ae2netanalyser;

import appeng.init.client.InitScreens;
import com.glodblock.github.ae2netanalyser.client.gui.GuiAnalyser;
import com.glodblock.github.ae2netanalyser.client.render.NetworkRender;
import com.glodblock.github.ae2netanalyser.container.ContainerAnalyser;
import com.glodblock.github.ae2netanalyser.network.AEANetworkHandler;
import com.glodblock.github.glodium.Glodium;
import com.glodblock.github.glodium.network.NetworkHandlerGiver;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;

public class AEAnalyzerClient extends AEAnalyzer implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        NetworkRender.hook();
        InitScreens.register(ContainerAnalyser.TYPE, GuiAnalyser::new, "/screens/network_analyser.json");
        AEANetworkHandler.init(NetworkHandlerGiver.create(Glodium.MODID, EnvType.CLIENT));
    }

}
