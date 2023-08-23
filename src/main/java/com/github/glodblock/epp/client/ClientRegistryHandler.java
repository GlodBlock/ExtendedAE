package com.github.glodblock.epp.client;

import appeng.init.client.InitScreens;
import appeng.menu.SlotSemantics;
import com.github.glodblock.epp.client.gui.GuiExIOBus;
import com.github.glodblock.epp.client.gui.GuiExInterface;
import com.github.glodblock.epp.client.gui.GuiExPatternProvider;
import com.github.glodblock.epp.container.ContainerExIOBus;
import com.github.glodblock.epp.container.ContainerExInterface;
import com.github.glodblock.epp.container.ContainerExPatternProvider;

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

    public void registerGui() {
        InitScreens.register(ContainerExPatternProvider.TYPE, GuiExPatternProvider::new, "/screens/ex_pattern_provider.json");
        InitScreens.register(ContainerExInterface.TYPE, GuiExInterface::new, "/screens/ex_interface.json");
        InitScreens.register(ContainerExIOBus.EXPORT_TYPE, GuiExIOBus::new, "/screens/ex_export_bus.json");
        InitScreens.register(ContainerExIOBus.IMPORT_TYPE, GuiExIOBus::new, "/screens/ex_import_bus.json");
    }

}