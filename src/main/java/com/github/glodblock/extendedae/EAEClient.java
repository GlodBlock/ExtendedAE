package com.github.glodblock.extendedae;

import appeng.api.IAEAddonEntrypoint;
import com.github.glodblock.extendedae.client.ClientRegistryHandler;
import com.github.glodblock.extendedae.client.hotkey.PatternHotKey;
import com.github.glodblock.extendedae.client.render.HighlightRender;
import com.github.glodblock.extendedae.network.EAENetworkClient;

public class EAEClient extends EAE implements IAEAddonEntrypoint {

    @Override
    public void onAe2Initialized() {
        ClientRegistryHandler.INSTANCE.init();
        PatternHotKey.init();
        HighlightRender.install();
        new EAENetworkClient();
    }

}
