package com.github.glodblock.extendedae;

import com.github.glodblock.extendedae.client.ClientRegistryHandler;
import com.github.glodblock.extendedae.client.hotkey.PatternHotKey;
import com.github.glodblock.extendedae.client.render.HighlightRender;
import net.fabricmc.api.ClientModInitializer;

public class EAEClient extends EAE implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientRegistryHandler.INSTANCE.init();
        PatternHotKey.init();
        HighlightRender.install();
    }

}
