package com.glodblock.github.extendedae.xmod;

import net.neoforged.fml.ModList;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ModConstants {

    public static Set<String> MOD_NAME = ModList.get().getMods().stream().flatMap(x -> Stream.of(x.getModId(), x.getDisplayName())).collect(Collectors.toSet());

    public static final String REI = "roughlyenoughitems";
    public static final String EMI = "emi";
    public static final String AE_JEI = "ae2jeiintegration";
    public static final String APPFLUX = "appflux";
    public static final String MEGA = "megacells";
    public static final String DARK_MODE = "darkmodeeverywhere";
    public static final String APPMEK = "appmek";
    public static final String MEK = "mekanism";
    public static final String FRAMED_BLOCKS = "framedblocks";
    public static final String APPPNEU = "appliedpneumatics";
    public static final String ADV_AE = "advanced_ae";
    public static final String APPLIED_E = "appliede";

}
