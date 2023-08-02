package com.glodblock.github.epp.client;

import appeng.init.client.InitScreens;
import appeng.menu.SlotSemantics;
import com.glodblock.github.epp.client.guis.GuiExIOBus;
import com.glodblock.github.epp.client.guis.GuiExInterface;
import com.glodblock.github.epp.client.guis.GuiExPatternProvider;
import com.glodblock.github.epp.container.ContainerExIOBus;
import com.glodblock.github.epp.container.ContainerExInterface;
import com.glodblock.github.epp.container.ContainerExPatternProvider;

public class ClientRegistryHandler {

    public static final ClientRegistryHandler INSTANCE = new ClientRegistryHandler();

    public void init() {
        this.registerSemantic();
        this.registerGui();
    }

    public void registerSemantic() {
        ExSemantics.EX_1 = SlotSemantics.register("EX_1", false);
        ExSemantics.EX_2 = SlotSemantics.register("EX_2", false);
        ExSemantics.EX_3 = SlotSemantics.register("EX_3", false);
        ExSemantics.EX_4 = SlotSemantics.register("EX_4", false);
    }

    private void registerGui() {
        InitScreens.register(ContainerExPatternProvider.TYPE, GuiExPatternProvider::new, "/screens/ex_pattern_provider.json");
        InitScreens.register(ContainerExInterface.TYPE, GuiExInterface::new, "/screens/ex_interface.json");
        InitScreens.register(ContainerExIOBus.EXPORT_TYPE, GuiExIOBus::new, "/screens/ex_export_bus.json");
        InitScreens.register(ContainerExIOBus.IMPORT_TYPE, GuiExIOBus::new, "/screens/ex_import_bus.json");
    }

}
