package com.glodblock.github.extendedae.xmod.wt;

import appeng.init.client.InitScreens;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class WTClientLoad {

    public static void init(RegisterMenuScreensEvent event) {
        InitScreens.register(event, ContainerUWirelessExPAT.TYPE, GuiUWirelessExPAT::new, "/screens/u_ex_pat.json");
    }

}
