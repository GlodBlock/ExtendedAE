package com.glodblock.github.epp.client;

import appeng.api.parts.PartModels;
import appeng.init.client.InitScreens;
import com.glodblock.github.epp.client.guis.GuiExPatternProvider;
import com.glodblock.github.epp.common.parts.PartExPatternProvider;
import com.glodblock.github.epp.container.ContainerExPatternProvider;

public class ClientRegistryHandler {

    public static final ClientRegistryHandler INSTANCE = new ClientRegistryHandler();

    public void init() {
        this.registerGui();
        this.registerModels();
    }

    private void registerGui() {
        InitScreens.register(ContainerExPatternProvider.TYPE, GuiExPatternProvider::new, "/screens/ex_pattern_provider.json");
    }

    private void registerModels() {
        PartModels.registerModels(PartExPatternProvider.MODELS);
    }

}
