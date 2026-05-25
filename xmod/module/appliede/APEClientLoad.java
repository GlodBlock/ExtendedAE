package com.glodblock.github.extendedae.xmod.appliede;

import appeng.init.client.InitScreens;
import com.glodblock.github.extendedae.client.gui.GuiExIOBus;
import com.glodblock.github.extendedae.xmod.appliede.containers.ContainerExEMCIOBus;
import com.glodblock.github.extendedae.xmod.appliede.containers.ContainerExEMCInterface;
import com.glodblock.github.extendedae.xmod.appliede.guis.GuiExEMCInterface;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class APEClientLoad {

    public static void init(RegisterMenuScreensEvent event) {
        InitScreens.register(event, ContainerExEMCInterface.TYPE, GuiExEMCInterface::new, "/screens/ex_emc_interface.json");
        InitScreens.register(event, ContainerExEMCIOBus.IMPORT_TYPE, GuiExIOBus::new, "/screens/ex_emc_import_bus.json");
        InitScreens.register(event, ContainerExEMCIOBus.EXPORT_TYPE, GuiExIOBus::new, "/screens/ex_emc_export_bus.json");
    }

}
