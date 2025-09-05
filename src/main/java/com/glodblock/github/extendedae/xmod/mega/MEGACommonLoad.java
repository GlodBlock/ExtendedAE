package com.glodblock.github.extendedae.xmod.mega;

import appeng.api.upgrades.Upgrades;
import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import gripe._90.megacells.definition.MEGAItems;

public class MEGACommonLoad {
    public static void init() {
        Upgrades.add(MEGAItems.GREATER_ENERGY_CARD, EPPItemAndBlock.WIRELESS_HUB, 4);
    }
}
