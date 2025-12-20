package com.glodblock.github.extendedae.xmod.appliede;

import appeng.api.parts.PartModels;
import appeng.items.parts.PartItem;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.EAERegistryHandler;
import com.glodblock.github.extendedae.xmod.appliede.blocks.BlockExEMCInterface;
import com.glodblock.github.extendedae.xmod.appliede.containers.ContainerExEMCInterface;
import com.glodblock.github.extendedae.xmod.appliede.items.ItemEMCInterfaceUpgrade;
import com.glodblock.github.extendedae.xmod.appliede.parts.PartExEMCInterface;
import com.glodblock.github.extendedae.xmod.appliede.tileentities.TileExEMCInterface;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

public class APESingletons {

    public static BlockExEMCInterface EX_EMC_INTERFACE;
    public static PartItem<PartExEMCInterface> EX_EMC_INTERFACE_PART;
    public static ItemEMCInterfaceUpgrade EMC_INTERFACE_UPGRADE;

    public static void init(EAERegistryHandler regHandler) {
        EX_EMC_INTERFACE = new BlockExEMCInterface();
        EX_EMC_INTERFACE_PART = new PartItem<>(new Item.Properties(), PartExEMCInterface.class, PartExEMCInterface::new);
        EMC_INTERFACE_UPGRADE = new ItemEMCInterfaceUpgrade();
        regHandler.block("ex_emc_interface", EX_EMC_INTERFACE, TileExEMCInterface.class, TileExEMCInterface::new);
        regHandler.item("ex_emc_interface_part", EX_EMC_INTERFACE_PART);
        regHandler.item("emc_interface_upgrade", EMC_INTERFACE_UPGRADE);
    }

    public static void register() {
        Registry.register(BuiltInRegistries.MENU, ExtendedAE.id("ex_emc_interface"), ContainerExEMCInterface.TYPE);
        PartModels.registerModels(PartExEMCInterface.MODELS);
    }

}
