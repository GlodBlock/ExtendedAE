package com.glodblock.github.extendedae.xmod.aae;

import com.glodblock.github.extendedae.client.gui.pattern.GuiProcessingPattern;
import com.glodblock.github.extendedae.container.pattern.ContainerAdvProcessingPattern;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class AAEClientLoad {

    public static void init(RegisterMenuScreensEvent event) {
        event.register(ContainerAdvProcessingPattern.TYPE, GuiProcessingPattern::new);
    }

}
