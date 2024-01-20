package com.glodblock.github.appflux.common;

import appeng.core.definitions.AEItems;
import appeng.items.materials.MaterialItem;
import com.glodblock.github.appflux.common.items.ItemFECell;
import com.glodblock.github.appflux.common.items.ItemGTEUCell;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.ModList;

public class AFItemAndBlock {

    public static MaterialItem FE_HOUSING;
    public static ItemFECell FE_CELL_1k;
    public static ItemFECell FE_CELL_4k;
    public static ItemFECell FE_CELL_16k;
    public static ItemFECell FE_CELL_64k;
    public static ItemFECell FE_CELL_256k;
    public static MaterialItem GTEU_HOUSING;
    public static ItemGTEUCell GTEU_CELL_1k;
    public static ItemGTEUCell GTEU_CELL_4k;
    public static ItemGTEUCell GTEU_CELL_16k;
    public static ItemGTEUCell GTEU_CELL_64k;
    public static ItemGTEUCell GTEU_CELL_256k;


    public static void init(AFRegistryHandler regHandler) {
        FE_HOUSING = new MaterialItem(new Item.Properties());
        FE_CELL_1k = new ItemFECell(AEItems.CELL_COMPONENT_1K, 1, 0.5);
        FE_CELL_4k = new ItemFECell(AEItems.CELL_COMPONENT_4K, 4, 1.0);
        FE_CELL_16k = new ItemFECell(AEItems.CELL_COMPONENT_16K, 16, 1.5);
        FE_CELL_64k = new ItemFECell(AEItems.CELL_COMPONENT_64K, 64, 2.0);
        FE_CELL_256k = new ItemFECell(AEItems.CELL_COMPONENT_256K, 256, 2.5);
        regHandler.item("fe_cell_housing", FE_HOUSING);
        regHandler.item("fe_1k_cell", FE_CELL_1k);
        regHandler.item("fe_4k_cell", FE_CELL_4k);
        regHandler.item("fe_16k_cell", FE_CELL_16k);
        regHandler.item("fe_64k_cell", FE_CELL_64k);
        regHandler.item("fe_256k_cell", FE_CELL_256k);
        if (ModList.get().isLoaded("gtceu")) {
            GTEU_HOUSING = new MaterialItem(new Item.Properties());
            GTEU_CELL_1k = new ItemGTEUCell(AEItems.CELL_COMPONENT_1K, 1, 0.5);
            GTEU_CELL_4k = new ItemGTEUCell(AEItems.CELL_COMPONENT_4K, 4, 1.0);
            GTEU_CELL_16k = new ItemGTEUCell(AEItems.CELL_COMPONENT_16K, 16, 1.5);
            GTEU_CELL_64k = new ItemGTEUCell(AEItems.CELL_COMPONENT_64K, 64, 2.0);
            GTEU_CELL_256k = new ItemGTEUCell(AEItems.CELL_COMPONENT_256K, 256, 2.5);
            regHandler.item("gteu_cell_housing", GTEU_HOUSING);
            regHandler.item("gteu_1k_cell", GTEU_CELL_1k);
            regHandler.item("gteu_4k_cell", GTEU_CELL_4k);
            regHandler.item("gteu_16k_cell", GTEU_CELL_16k);
            regHandler.item("gteu_64k_cell", GTEU_CELL_64k);
            regHandler.item("gteu_256k_cell", GTEU_CELL_256k);
        }
    }

}
