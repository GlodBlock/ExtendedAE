package com.glodblock.github.extendedae.xmod.pneumatics;

import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class APClientLoad {

    public static void init(RegisterMenuScreensEvent event) {
        event.register(ContainerAmadronPattern.TYPE, GuiAmadronPattern::new);
    }

}
