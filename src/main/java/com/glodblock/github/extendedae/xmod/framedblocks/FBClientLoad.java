package com.glodblock.github.extendedae.xmod.framedblocks;

import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class FBClientLoad {

    public static void init(RegisterMenuScreensEvent event) {
        event.register(ContainerFramingSawPattern.TYPE, GuiFramingSawPattern::new);
    }

}
