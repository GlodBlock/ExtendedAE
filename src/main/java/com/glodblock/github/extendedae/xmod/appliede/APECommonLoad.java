package com.glodblock.github.extendedae.xmod.appliede;

import appeng.api.upgrades.Upgrades;
import gripe._90.appliede.AppliedE;

public class APECommonLoad {

    public static void init() {
        Upgrades.add(AppliedE.LEARNING_CARD.get(), APESingletons.EX_EMC_INTERFACE, 1, "gui.extendedae.ex_emc_interface");
        Upgrades.add(AppliedE.LEARNING_CARD.get(), APESingletons.EX_EMC_INTERFACE_PART, 1, "gui.extendedae.ex_emc_interface");
    }

}
