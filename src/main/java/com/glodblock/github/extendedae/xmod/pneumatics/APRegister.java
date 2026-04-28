package com.glodblock.github.extendedae.xmod.pneumatics;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class APRegister {

    public static void register() {
        Registry.register(BuiltInRegistries.MENU, ContainerAmadronPattern.ID, ContainerAmadronPattern.TYPE);
    }

}
