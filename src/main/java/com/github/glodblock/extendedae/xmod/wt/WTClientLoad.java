package com.github.glodblock.extendedae.xmod.wt;

import appeng.init.client.InitScreens;

public class WTClientLoad {

    public static void init() {
        InitScreens.register(ContainerUWirelessExPAT.TYPE, GuiUWirelessExPAT::new, "/screens/u_ex_pat.json");
    }

}
