package com.glodblock.github.epp;

import com.glodblock.github.epp.client.ClientRegistryHandler;
import net.fabricmc.api.ClientModInitializer;

public class EPPClient extends EPP implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientRegistryHandler.INSTANCE.init();
    }

}
