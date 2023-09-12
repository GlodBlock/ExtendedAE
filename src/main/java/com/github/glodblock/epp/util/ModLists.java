package com.github.glodblock.epp.util;

import net.minecraftforge.fml.ModList;

public class ModLists {

    public static boolean A_MEK = false;
    public static boolean A_BOT = false;

    public static void init() {
        A_MEK = ModList.get().isLoaded("appmek");
        A_BOT = ModList.get().isLoaded("appbot");
    }

}
