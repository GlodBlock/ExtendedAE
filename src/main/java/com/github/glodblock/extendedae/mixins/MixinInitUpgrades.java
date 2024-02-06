package com.github.glodblock.extendedae.mixins;

import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;
import appeng.init.internal.InitUpgrades;
import com.github.glodblock.extendedae.common.EAEItemAndBlock;
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
        Upgrades.add(AEItems.FUZZY_CARD, EAEItemAndBlock.EX_INTERFACE.asItem(), 1, "gui.expatternprovider.ex_interface");
        Upgrades.add(AEItems.CRAFTING_CARD, EAEItemAndBlock.EX_INTERFACE.asItem(), 1, "gui.expatternprovider.ex_interface");
        Upgrades.add(AEItems.FUZZY_CARD, EAEItemAndBlock.EX_INTERFACE_PART.asItem(), 1, "gui.expatternprovider.ex_interface");
        Upgrades.add(AEItems.CRAFTING_CARD, EAEItemAndBlock.EX_INTERFACE_PART.asItem(), 1, "gui.expatternprovider.ex_interface");
        Upgrades.add(AEItems.VOID_CARD, EAEItemAndBlock.INFINITY_CELL, 1, "item.expatternprovider.infinity_cell");
        Upgrades.add(AEItems.CAPACITY_CARD, EAEItemAndBlock.EX_EXPORT_BUS, 5, "group.ex_io_bus_part");
        Upgrades.add(AEItems.REDSTONE_CARD, EAEItemAndBlock.EX_EXPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.SPEED_CARD, EAEItemAndBlock.EX_EXPORT_BUS, 4, "group.ex_io_bus_part");
        Upgrades.add(AEItems.INVERTER_CARD, EAEItemAndBlock.EX_EXPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.FUZZY_CARD, EAEItemAndBlock.EX_EXPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.CRAFTING_CARD, EAEItemAndBlock.EX_EXPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.CAPACITY_CARD, EAEItemAndBlock.EX_IMPORT_BUS, 5, "group.ex_io_bus_part");
        Upgrades.add(AEItems.REDSTONE_CARD, EAEItemAndBlock.EX_IMPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.SPEED_CARD, EAEItemAndBlock.EX_IMPORT_BUS, 4, "group.ex_io_bus_part");
        Upgrades.add(AEItems.INVERTER_CARD, EAEItemAndBlock.EX_IMPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.FUZZY_CARD, EAEItemAndBlock.EX_IMPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.ENERGY_CARD, EAEItemAndBlock.WIRELESS_CONNECTOR, 4, "gui.expatternprovider.wireless_connect");
        Upgrades.add(AEItems.SPEED_CARD, EAEItemAndBlock.EX_ASSEMBLER, 5, "gui.expatternprovider.ex_molecular_assembler");
        Upgrades.add(AEItems.SPEED_CARD, EAEItemAndBlock.EX_INSCRIBER, 4, "gui.expatternprovider.ex_inscriber");
        Upgrades.add(AEItems.INVERTER_CARD, EAEItemAndBlock.TAG_STORAGE_BUS, 1, "item.expatternprovider.tag_storage_bus");
        Upgrades.add(AEItems.VOID_CARD, EAEItemAndBlock.TAG_STORAGE_BUS, 1, "item.expatternprovider.tag_storage_bus");
        Upgrades.add(AEItems.REDSTONE_CARD, EAEItemAndBlock.TAG_EXPORT_BUS, 1, "item.expatternprovider.tag_export_bus");
        Upgrades.add(AEItems.SPEED_CARD, EAEItemAndBlock.TAG_EXPORT_BUS, 4, "item.expatternprovider.tag_export_bus");
        Upgrades.add(AEItems.FUZZY_CARD, EAEItemAndBlock.THRESHOLD_LEVEL_EMITTER, 1, "item.expatternprovider.threshold_level_emitter");
        Upgrades.add(AEItems.INVERTER_CARD, EAEItemAndBlock.MOD_STORAGE_BUS, 1, "item.expatternprovider.mod_storage_bus");
        Upgrades.add(AEItems.VOID_CARD, EAEItemAndBlock.MOD_STORAGE_BUS, 1, "item.expatternprovider.mod_storage_bus");
        Upgrades.add(AEItems.REDSTONE_CARD, EAEItemAndBlock.MOD_EXPORT_BUS, 1, "item.expatternprovider.mod_export_bus");
        Upgrades.add(AEItems.SPEED_CARD, EAEItemAndBlock.MOD_EXPORT_BUS, 4, "item.expatternprovider.mod_export_bus");
        Upgrades.add(AEItems.FUZZY_CARD, EAEItemAndBlock.ACTIVE_FORMATION_PLANE, 1, "item.expatternprovider.active_formation_plane");
        Upgrades.add(AEItems.CAPACITY_CARD, EAEItemAndBlock.ACTIVE_FORMATION_PLANE, 5, "item.expatternprovider.active_formation_plane");
    }

}
