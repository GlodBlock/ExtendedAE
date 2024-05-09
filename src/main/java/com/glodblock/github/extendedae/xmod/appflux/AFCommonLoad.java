package com.glodblock.github.extendedae.xmod.appflux;

import appeng.api.upgrades.Upgrades;
import appeng.core.localization.GuiText;
import com.glodblock.github.appflux.common.AFItemAndBlock;
import com.glodblock.github.appflux.xmod.wirelesscharger.ChargerBlacklist;
import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import com.glodblock.github.extendedae.common.tileentities.TileIngredientBuffer;

public class AFCommonLoad {

    public static void init() {
        try {
            ChargerBlacklist.BLACKLIST.add(te -> te instanceof TileIngredientBuffer);
            Upgrades.add(AFItemAndBlock.INDUCTION_CARD, EPPItemAndBlock.EX_INTERFACE, 1, GuiText.Interface.getTranslationKey());
            Upgrades.add(AFItemAndBlock.INDUCTION_CARD, EPPItemAndBlock.EX_INTERFACE_PART, 1, GuiText.Interface.getTranslationKey());
            Upgrades.add(AFItemAndBlock.INDUCTION_CARD, EPPItemAndBlock.EX_PATTERN_PROVIDER, 1, "group.pattern_provider.name");
            Upgrades.add(AFItemAndBlock.INDUCTION_CARD, EPPItemAndBlock.EX_PATTERN_PROVIDER_PART, 1, "group.pattern_provider.name");
        } catch (Throwable ignored) {
            // NO-OP
        }
    }

}
