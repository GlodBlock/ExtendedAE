package com.glodblock.github.extendedae.xmod.megacells;

import appeng.api.upgrades.Upgrades;
import com.glodblock.github.extendedae.common.EAESingletons;
import gripe._90.megacells.definition.MEGAItems;

public class MEGACommonLoad {

    public static void init() {
        Upgrades.add(MEGAItems.GREATER_ENERGY_CARD, EAESingletons.WIRELESS_CONNECTOR, 4);
        Upgrades.add(MEGAItems.GREATER_ENERGY_CARD, EAESingletons.WIRELESS_HUB, 4);
    }

}
