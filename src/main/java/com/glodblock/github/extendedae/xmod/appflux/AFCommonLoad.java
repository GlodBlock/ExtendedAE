package com.glodblock.github.extendedae.xmod.appflux;

import appeng.api.upgrades.Upgrades;
import com.glodblock.github.appflux.common.AFItemAndBlock;
import com.glodblock.github.appflux.xmod.wc.ChargerBlacklist;
import com.glodblock.github.extendedae.common.EAEItemAndBlock;
import com.glodblock.github.extendedae.common.tileentities.TileIngredientBuffer;

public class AFCommonLoad {

    public static void init() {
        try {
            ChargerBlacklist.BLACKLIST.add(te -> te instanceof TileIngredientBuffer);
            Upgrades.add(AFItemAndBlock.INDUCTION_CARD, EAEItemAndBlock.EX_INTERFACE, 1, "gui.extendedae.ex_interface");
            Upgrades.add(AFItemAndBlock.INDUCTION_CARD, EAEItemAndBlock.EX_INTERFACE_PART, 1, "gui.extendedae.ex_interface");
            Upgrades.add(AFItemAndBlock.INDUCTION_CARD, EAEItemAndBlock.EX_PATTERN_PROVIDER, 1, "gui.extendedae.ex_pattern_provider");
            Upgrades.add(AFItemAndBlock.INDUCTION_CARD, EAEItemAndBlock.EX_PATTERN_PROVIDER_PART, 1, "gui.extendedae.ex_pattern_provider");
        } catch (Throwable ignored) {
            // NO-OP
        }
    }

}
