package com.glodblock.github.extendedae.xmod.aae;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class AAERegister {

    public static void register() {
        Registry.register(BuiltInRegistries.MENU, ContainerAdvProcessingPattern.ID, ContainerAdvProcessingPattern.TYPE);
    }

}
