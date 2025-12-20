package com.glodblock.github.extendedae.xmod.appliede;

import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;
import gripe._90.appliede.AppliedE;

public class APECommonLoad {

    public static void init() {
        Upgrades.add(AppliedE.LEARNING_CARD.get(), APESingletons.EX_EMC_INTERFACE, 1, "gui.extendedae.ex_emc_interface");
        Upgrades.add(AppliedE.LEARNING_CARD.get(), APESingletons.EX_EMC_INTERFACE_PART, 1, "gui.extendedae.ex_emc_interface");
        Upgrades.add(AppliedE.LEARNING_CARD.get(), APESingletons.EX_EMC_IMPORT_BUS, 1);
        Upgrades.add(AEItems.REDSTONE_CARD, APESingletons.EX_EMC_IMPORT_BUS, 1, "group.ex_emc_io_bus_part");
        Upgrades.add(AEItems.CAPACITY_CARD, APESingletons.EX_EMC_IMPORT_BUS, 5, "group.ex_emc_io_bus_part");
        Upgrades.add(AEItems.SPEED_CARD, APESingletons.EX_EMC_IMPORT_BUS, 4, "group.ex_emc_io_bus_part");
        Upgrades.add(AEItems.INVERTER_CARD, APESingletons.EX_EMC_IMPORT_BUS, 1, "group.ex_emc_io_bus_part");
        Upgrades.add(AEItems.REDSTONE_CARD, APESingletons.EX_EMC_EXPORT_BUS, 1, "group.ex_emc_io_bus_part");
        Upgrades.add(AEItems.CAPACITY_CARD, APESingletons.EX_EMC_EXPORT_BUS, 5, "group.ex_emc_io_bus_part");
        Upgrades.add(AEItems.SPEED_CARD, APESingletons.EX_EMC_EXPORT_BUS, 4, "group.ex_emc_io_bus_part");
    }

}
