package com.glodblock.github.extendedae.xmod.pneumatics;

import com.glodblock.github.extendedae.container.pattern.PatternGuiHandler;

public class APCommonLoad {

    private static Class<?> PATTERN_CLASS;

    public static void init() {
        try {
            if (PATTERN_CLASS == null) {
                PATTERN_CLASS = Class.forName("com.wintercogs.appliedpneumatics.common.me.crafting.AmadronPatternDetails");
            }
            PatternGuiHandler.addPatternHandler(PATTERN_CLASS, ContainerAmadronPattern.ID);
        } catch (Throwable ignored) {
            // NO-OP
        }
    }

}
