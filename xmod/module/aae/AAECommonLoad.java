package com.glodblock.github.extendedae.xmod.aae;

import com.glodblock.github.extendedae.container.pattern.PatternGuiHandler;
import net.pedroksl.advanced_ae.common.patterns.AdvProcessingPattern;

public class AAECommonLoad {

    public static void init() {
        PatternGuiHandler.addPatternHandler(AdvProcessingPattern.class, ContainerAdvProcessingPattern.ID);
    }

}
