package com.github.glodblock.epp.xmod;

import net.minecraftforge.fml.ModList;

public final class LoadList {

    public static boolean JEI = false;
    public static boolean REI = false;

    public static void init() {
        var list = ModList.get();
        if (list.isLoaded("jei")) {
            JEI = true;
        }
        if (list.isLoaded("roughlyenoughitems")) {
            REI = true;
        }
    }

}
