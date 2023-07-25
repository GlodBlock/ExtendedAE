package com.github.glodblock.epp.client;

import appeng.api.parts.PartModels;
import appeng.init.client.InitScreens;
import com.github.glodblock.epp.client.gui.GuiExPatternProvider;
import com.github.glodblock.epp.common.parts.PartExPatternProvider;
import com.github.glodblock.epp.container.ContainerExPatternProvider;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientRegistryHandler {

    public static final ClientRegistryHandler INSTANCE = new ClientRegistryHandler();

    public void init() {
        this.registerGui();
        this.registerModels();
    }

    public void registerGui() {
        InitScreens.register(ContainerExPatternProvider.TYPE, GuiExPatternProvider::new, "/screens/ex_pattern_provider.json");
    }

    public void registerModels() {
        PartModels.registerModels(PartExPatternProvider.MODELS);
    }

}
