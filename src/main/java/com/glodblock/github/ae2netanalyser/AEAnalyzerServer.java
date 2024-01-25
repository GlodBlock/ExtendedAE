package com.glodblock.github.ae2netanalyser;

import com.glodblock.github.ae2netanalyser.common.AEAItems;
import com.glodblock.github.ae2netanalyser.common.AEARegistryHandler;
import com.glodblock.github.ae2netanalyser.common.me.tracker.PlayerTracker;
import com.glodblock.github.ae2netanalyser.network.AEANetworkHandler;
import com.glodblock.github.glodium.Glodium;
import com.glodblock.github.glodium.network.NetworkHandlerGiver;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;

public class AEAnalyzerServer extends AEAnalyzer implements ModInitializer {

    @Override
    public void onInitialize() {
        AEAItems.init(AEARegistryHandler.INSTANCE);
        AEARegistryHandler.INSTANCE.runRegister();
        PlayerTracker.init();
        AEARegistryHandler.INSTANCE.init();
        AEANetworkHandler.init(NetworkHandlerGiver.create(Glodium.MODID, EnvType.SERVER));
    }

}
