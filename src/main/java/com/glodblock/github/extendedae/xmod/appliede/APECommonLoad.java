package com.glodblock.github.extendedae.xmod.appliede;

import appeng.api.parts.PartModels;
import appeng.api.upgrades.Upgrades;
import appeng.core.AppEng;
import appeng.core.definitions.AEItems;
import appeng.items.parts.PartItem;
import com.glodblock.github.extendedae.common.EAERegistryHandler;
import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import com.glodblock.github.extendedae.xmod.appliede.blocks.BlockExEMCInterface;
import com.glodblock.github.extendedae.xmod.appliede.containers.ContainerExEMCIOBus;
import com.glodblock.github.extendedae.xmod.appliede.containers.ContainerExEMCInterface;
import com.glodblock.github.extendedae.xmod.appliede.items.ItemEMCIOBusUpgrade;
import com.glodblock.github.extendedae.xmod.appliede.items.ItemEMCInterfaceUpgrade;
import com.glodblock.github.extendedae.xmod.appliede.parts.PartExEMCExportBus;
import com.glodblock.github.extendedae.xmod.appliede.parts.PartExEMCImportBus;
import com.glodblock.github.extendedae.xmod.appliede.parts.PartExEMCInterface;
import com.glodblock.github.extendedae.xmod.appliede.tileentities.TileExEMCInterface;
import gripe._90.appliede.AppliedE;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public class APECommonLoad {

    public static void initSingleton(EAERegistryHandler regHandler) {
        EPPItemAndBlock.EX_EMC_INTERFACE = new BlockExEMCInterface();
        EPPItemAndBlock.EX_EMC_INTERFACE_PART = new PartItem<>(new Item.Properties(), PartExEMCInterface.class, PartExEMCInterface::new);
        EPPItemAndBlock.EMC_INTERFACE_UPGRADE = new ItemEMCInterfaceUpgrade();
        EPPItemAndBlock.EX_EMC_IMPORT_BUS = new PartItem<>(new Item.Properties(), PartExEMCImportBus.class, PartExEMCImportBus::new);
        EPPItemAndBlock.EX_EMC_EXPORT_BUS = new PartItem<>(new Item.Properties(), PartExEMCExportBus.class, PartExEMCExportBus::new);
        EPPItemAndBlock.EMC_IO_BUS_UPGRADE = new ItemEMCIOBusUpgrade();
        regHandler.block("ex_emc_interface", (BlockExEMCInterface) EPPItemAndBlock.EX_EMC_INTERFACE, TileExEMCInterface.class, TileExEMCInterface::new);
        regHandler.item("ex_emc_interface_part", EPPItemAndBlock.EX_EMC_INTERFACE_PART);
        regHandler.item("emc_interface_upgrade", EPPItemAndBlock.EMC_INTERFACE_UPGRADE);
        regHandler.item("ex_emc_import_bus_part", EPPItemAndBlock.EX_EMC_IMPORT_BUS);
        regHandler.item("ex_emc_export_bus_part", EPPItemAndBlock.EX_EMC_EXPORT_BUS);
        regHandler.item("emc_io_bus_upgrade", EPPItemAndBlock.EMC_IO_BUS_UPGRADE);
    }

    public static void container() {
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("ex_emc_interface"), ContainerExEMCInterface.TYPE);
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("ex_emc_import_bus"), ContainerExEMCIOBus.IMPORT_TYPE);
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("ex_emc_export_bus"), ContainerExEMCIOBus.EXPORT_TYPE);
        PartModels.registerModels(PartExEMCInterface.MODELS);
        PartModels.registerModels(PartExEMCImportBus.MODELS);
        PartModels.registerModels(PartExEMCExportBus.MODELS);
    }

    public static void init() {
        Upgrades.add(AppliedE.LEARNING_CARD.get(), EPPItemAndBlock.EX_EMC_INTERFACE, 1, "gui.expatternprovider.ex_emc_interface");
        Upgrades.add(AppliedE.LEARNING_CARD.get(), EPPItemAndBlock.EX_EMC_INTERFACE_PART, 1, "gui.expatternprovider.ex_emc_interface");
        Upgrades.add(AppliedE.LEARNING_CARD.get(), EPPItemAndBlock.EX_EMC_IMPORT_BUS, 1);
        Upgrades.add(AEItems.REDSTONE_CARD, EPPItemAndBlock.EX_EMC_IMPORT_BUS, 1, "group.ex_emc_io_bus_part");
        Upgrades.add(AEItems.CAPACITY_CARD, EPPItemAndBlock.EX_EMC_IMPORT_BUS, 5, "group.ex_emc_io_bus_part");
        Upgrades.add(AEItems.SPEED_CARD, EPPItemAndBlock.EX_EMC_IMPORT_BUS, 4, "group.ex_emc_io_bus_part");
        Upgrades.add(AEItems.INVERTER_CARD, EPPItemAndBlock.EX_EMC_IMPORT_BUS, 1, "group.ex_emc_io_bus_part");
        Upgrades.add(AEItems.REDSTONE_CARD, EPPItemAndBlock.EX_EMC_EXPORT_BUS, 1, "group.ex_emc_io_bus_part");
        Upgrades.add(AEItems.CAPACITY_CARD, EPPItemAndBlock.EX_EMC_EXPORT_BUS, 5, "group.ex_emc_io_bus_part");
        Upgrades.add(AEItems.SPEED_CARD, EPPItemAndBlock.EX_EMC_EXPORT_BUS, 4, "group.ex_emc_io_bus_part");
    }

}
