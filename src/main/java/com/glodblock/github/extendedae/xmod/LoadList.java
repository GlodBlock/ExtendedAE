package com.glodblock.github.extendedae.xmod;

import net.minecraftforge.fml.ModList;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class LoadList {

    public static boolean JEI = false;
    public static boolean REI = false;

    public static Set<String> MOD_NAME = ModList.get().getMods().stream().flatMap(x -> Stream.of(x.getModId(), x.getDisplayName())).collect(Collectors.toSet());

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
