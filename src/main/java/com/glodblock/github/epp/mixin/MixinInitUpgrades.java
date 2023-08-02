package com.glodblock.github.epp.mixin;

import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;
import appeng.init.internal.InitUpgrades;
import com.glodblock.github.epp.common.EPPItemAndBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InitUpgrades.class)
public abstract class MixinInitUpgrades {

    @Inject(
            method = "init",
            at = @At("TAIL"),
            remap = false
    )
    private static void addUpgrade(CallbackInfo ci) {
        Upgrades.add(AEItems.FUZZY_CARD, EPPItemAndBlock.EX_INTERFACE, 1, "gui.expatternprovider.ex_interface");
        Upgrades.add(AEItems.CRAFTING_CARD, EPPItemAndBlock.EX_INTERFACE, 1, "gui.expatternprovider.ex_interface");
        Upgrades.add(AEItems.FUZZY_CARD, EPPItemAndBlock.EX_INTERFACE_PART, 1, "gui.expatternprovider.ex_interface");
        Upgrades.add(AEItems.CRAFTING_CARD, EPPItemAndBlock.EX_INTERFACE_PART, 1, "gui.expatternprovider.ex_interface");
        Upgrades.add(AEItems.VOID_CARD, EPPItemAndBlock.INFINITY_CELL, 1, "item.expatternprovider.infinity_cell");
        Upgrades.add(AEItems.CAPACITY_CARD, EPPItemAndBlock.EX_EXPORT_BUS, 2, "group.ex_io_bus_part");
        Upgrades.add(AEItems.REDSTONE_CARD, EPPItemAndBlock.EX_EXPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.SPEED_CARD, EPPItemAndBlock.EX_EXPORT_BUS, 4, "group.ex_io_bus_part");
        Upgrades.add(AEItems.INVERTER_CARD, EPPItemAndBlock.EX_EXPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.FUZZY_CARD, EPPItemAndBlock.EX_EXPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.CAPACITY_CARD, EPPItemAndBlock.EX_IMPORT_BUS, 2, "group.ex_io_bus_part");
        Upgrades.add(AEItems.REDSTONE_CARD, EPPItemAndBlock.EX_IMPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.SPEED_CARD, EPPItemAndBlock.EX_IMPORT_BUS, 4, "group.ex_io_bus_part");
        Upgrades.add(AEItems.INVERTER_CARD, EPPItemAndBlock.EX_IMPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.FUZZY_CARD, EPPItemAndBlock.EX_IMPORT_BUS, 1, "group.ex_io_bus_part");
    }

}
