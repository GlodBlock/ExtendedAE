package com.glodblock.github.extendedae.xmod.appflux;

import com.glodblock.github.appflux.xmod.wirelesscharger.ChargerBlacklist;
import com.glodblock.github.extendedae.common.tileentities.TileIngredientBuffer;

public class AFCommonLoad {

    public static void init() {
        try {
            ChargerBlacklist.BLACKLIST.add(te -> te instanceof TileIngredientBuffer);
        } catch (Throwable ignored) {
            // NO-OP
        }
    }

}
