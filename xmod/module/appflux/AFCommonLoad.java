package com.glodblock.github.extendedae.xmod.appflux;

import appeng.api.upgrades.Upgrades;
import com.glodblock.github.appflux.common.AFSingletons;
import com.glodblock.github.appflux.xmod.jade.JadeBlacklist;
import com.glodblock.github.appflux.xmod.wc.ChargerBlacklist;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.common.tileentities.TileCaner;
import com.glodblock.github.extendedae.common.tileentities.TileIngredientBuffer;

public class AFCommonLoad {

    public static void init() {
        try {
            ChargerBlacklist.BLACKLIST.add(te -> te instanceof TileIngredientBuffer);
            JadeBlacklist.BLACK_LIST.add(o -> o instanceof TileIngredientBuffer);
            JadeBlacklist.BLACK_LIST.add(o -> o instanceof TileCaner);
            Upgrades.add(AFSingletons.INDUCTION_CARD, EAESingletons.EX_INTERFACE, 1, "gui.extendedae.ex_interface");
            Upgrades.add(AFSingletons.INDUCTION_CARD, EAESingletons.EX_INTERFACE_PART, 1, "gui.extendedae.ex_interface");
            Upgrades.add(AFSingletons.INDUCTION_CARD, EAESingletons.EX_PATTERN_PROVIDER, 1, "block.extendedae.ex_pattern_provider");
            Upgrades.add(AFSingletons.INDUCTION_CARD, EAESingletons.EX_PATTERN_PROVIDER_PART, 1, "block.extendedae.ex_pattern_provider");
            Upgrades.add(AFSingletons.INDUCTION_CARD, EAESingletons.OVERSIZE_INTERFACE, 1, "gui.extendedae.oversize_interface");
            Upgrades.add(AFSingletons.INDUCTION_CARD, EAESingletons.OVERSIZE_INTERFACE_PART, 1, "gui.extendedae.oversize_interface");
        } catch (Throwable ignored) {
            // NO-OP
        }
    }

}
