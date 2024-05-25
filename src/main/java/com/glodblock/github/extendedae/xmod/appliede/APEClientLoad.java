package com.glodblock.github.extendedae.xmod.appliede;

import appeng.init.client.InitScreens;
import com.glodblock.github.extendedae.client.gui.GuiExIOBus;
import com.glodblock.github.extendedae.xmod.appliede.containers.ContainerExEMCIOBus;
import com.glodblock.github.extendedae.xmod.appliede.containers.ContainerExEMCInterface;
import com.glodblock.github.extendedae.xmod.appliede.guis.GuiExEMCInterface;

public class APEClientLoad {

    public static void init() {
        InitScreens.register(ContainerExEMCInterface.TYPE, GuiExEMCInterface::new, "/screens/ex_emc_interface.json");
        InitScreens.register(ContainerExEMCIOBus.IMPORT_TYPE, GuiExIOBus::new, "/screens/ex_emc_import_bus.json");
        InitScreens.register(ContainerExEMCIOBus.EXPORT_TYPE, GuiExIOBus::new, "/screens/ex_emc_export_bus.json");
    }

}
