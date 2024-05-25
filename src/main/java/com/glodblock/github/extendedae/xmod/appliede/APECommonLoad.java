package com.glodblock.github.extendedae.xmod.appliede;

import appeng.api.parts.PartModels;
import appeng.api.upgrades.Upgrades;
import appeng.core.AppEng;
import appeng.items.parts.PartItem;
import com.glodblock.github.extendedae.common.EAERegistryHandler;
import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import com.glodblock.github.extendedae.xmod.appliede.blocks.BlockExEMCInterface;
import com.glodblock.github.extendedae.xmod.appliede.containers.ContainerExEMCInterface;
import com.glodblock.github.extendedae.xmod.appliede.items.ItemEMCInterfaceUpgrade;
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
        regHandler.block("ex_emc_interface", (BlockExEMCInterface) EPPItemAndBlock.EX_EMC_INTERFACE, TileExEMCInterface.class, TileExEMCInterface::new);
        regHandler.item("ex_emc_interface_part", EPPItemAndBlock.EX_EMC_INTERFACE_PART);
        regHandler.item("emc_interface_upgrade", EPPItemAndBlock.EMC_INTERFACE_UPGRADE);
    }

    public static void container() {
        ForgeRegistries.MENU_TYPES.register(AppEng.makeId("ex_emc_interface"), ContainerExEMCInterface.TYPE);
        PartModels.registerModels(PartExEMCInterface.MODELS);
    }

    public static void init() {
        Upgrades.add(AppliedE.LEARNING_CARD.get(), EPPItemAndBlock.EX_EMC_INTERFACE, 1, "gui.expatternprovider.ex_emc_interface");
        Upgrades.add(AppliedE.LEARNING_CARD.get(), EPPItemAndBlock.EX_EMC_INTERFACE_PART, 1, "gui.expatternprovider.ex_emc_interface");
    }

}
