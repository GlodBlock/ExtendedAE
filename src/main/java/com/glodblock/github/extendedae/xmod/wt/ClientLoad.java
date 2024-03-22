package com.glodblock.github.extendedae.xmod.wt;

import appeng.init.client.InitScreens;
import com.glodblock.github.extendedae.client.gui.GuiExPatternTerminal;
import com.glodblock.github.extendedae.container.ContainerExPatternTerminal;

public class ClientLoad {

    public static void init() {
        InitScreens.register(ContainerUWirelessExPAT.TYPE, GuiUWirelessExPAT::new, "/screens/u_ex_pat.json");
    }

}
