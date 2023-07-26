package com.github.glodblock.epp.client;

import appeng.api.parts.PartModels;
import appeng.init.client.InitScreens;
import appeng.menu.SlotSemantics;
import com.github.glodblock.epp.client.gui.GuiExInterface;
import com.github.glodblock.epp.client.gui.GuiExPatternProvider;
import com.github.glodblock.epp.common.parts.PartExInterface;
import com.github.glodblock.epp.common.parts.PartExPatternProvider;
import com.github.glodblock.epp.container.ContainerExInterface;
import com.github.glodblock.epp.container.ContainerExPatternProvider;

public class ClientRegistryHandler {

    public static final ClientRegistryHandler INSTANCE = new ClientRegistryHandler();

    public void init() {
        this.registerSemantic();
        this.registerGui();
        this.registerModels();
    }

    public void registerSemantic() {
        ExSemantics.EX_1 = SlotSemantics.register("EX_1", false);
        ExSemantics.EX_2 = SlotSemantics.register("EX_2", false);
        ExSemantics.EX_3 = SlotSemantics.register("EX_3", false);
        ExSemantics.EX_4 = SlotSemantics.register("EX_4", false);
    }

    public void registerGui() {
        InitScreens.register(ContainerExPatternProvider.TYPE, GuiExPatternProvider::new, "/screens/ex_pattern_provider.json");
        InitScreens.register(ContainerExInterface.TYPE, GuiExInterface::new, "/screens/ex_interface.json");
    }

    public void registerModels() {
        PartModels.registerModels(PartExPatternProvider.MODELS);
        PartModels.registerModels(PartExInterface.MODELS);
    }

}
