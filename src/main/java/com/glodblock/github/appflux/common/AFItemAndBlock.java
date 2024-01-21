package com.glodblock.github.appflux.common;

import appeng.core.definitions.AEItems;
import appeng.items.materials.MaterialItem;
import com.glodblock.github.appflux.common.blocks.BlockFluxAccessor;
import com.glodblock.github.appflux.common.items.ItemFECell;
import com.glodblock.github.appflux.common.items.ItemGTEUCell;
import com.glodblock.github.appflux.common.tileentities.TileFluxAccessor;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.ModList;

public class AFItemAndBlock {

    public static MaterialItem CORE_1k;
    public static MaterialItem CORE_4k;
    public static MaterialItem CORE_16k;
    public static MaterialItem CORE_64k;
    public static MaterialItem CORE_256k;
    public static MaterialItem CHARGED_REDSTONE;
    public static MaterialItem ENERGY_PROCESSOR_PRINT;
    public static MaterialItem ENERGY_PROCESSOR_PRESS;
    public static MaterialItem ENERGY_PROCESSOR;
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
    public static BlockFluxAccessor FLUX_ACCESSOR;

    public static void init(AFRegistryHandler regHandler) {
        CORE_1k = new MaterialItem(new Item.Properties());
        CORE_4k = new MaterialItem(new Item.Properties());
        CORE_16k = new MaterialItem(new Item.Properties());
        CORE_64k = new MaterialItem(new Item.Properties());
        CORE_256k = new MaterialItem(new Item.Properties());
        CHARGED_REDSTONE = new MaterialItem(new Item.Properties());
        ENERGY_PROCESSOR = new MaterialItem(new Item.Properties());
        ENERGY_PROCESSOR_PRINT = new MaterialItem(new Item.Properties());
        ENERGY_PROCESSOR_PRESS = new MaterialItem(new Item.Properties());
        FE_HOUSING = new MaterialItem(new Item.Properties());
        FE_CELL_1k = new ItemFECell(CORE_1k, 1, 0.5);
        FE_CELL_4k = new ItemFECell(CORE_4k, 4, 1.0);
        FE_CELL_16k = new ItemFECell(CORE_16k, 16, 1.5);
        FE_CELL_64k = new ItemFECell(CORE_64k, 64, 2.0);
        FE_CELL_256k = new ItemFECell(CORE_256k, 256, 2.5);
        FLUX_ACCESSOR = new BlockFluxAccessor();
        regHandler.item("core_1k", CORE_1k);
        regHandler.item("core_4k", CORE_4k);
        regHandler.item("core_16k", CORE_16k);
        regHandler.item("core_64k", CORE_64k);
        regHandler.item("core_256k", CORE_256k);
        regHandler.item("charged_redstone", CHARGED_REDSTONE);
        regHandler.item("energy_processor", ENERGY_PROCESSOR);
        regHandler.item("printed_energy_processor", ENERGY_PROCESSOR_PRINT);
        regHandler.item("energy_processor_press", ENERGY_PROCESSOR_PRESS);
        regHandler.item("fe_cell_housing", FE_HOUSING);
        regHandler.item("fe_1k_cell", FE_CELL_1k);
        regHandler.item("fe_4k_cell", FE_CELL_4k);
        regHandler.item("fe_16k_cell", FE_CELL_16k);
        regHandler.item("fe_64k_cell", FE_CELL_64k);
        regHandler.item("fe_256k_cell", FE_CELL_256k);
        regHandler.block("flux_accessor", FLUX_ACCESSOR, TileFluxAccessor.class, TileFluxAccessor::new);
        if (ModList.get().isLoaded("gtceu")) {
            GTEU_HOUSING = new MaterialItem(new Item.Properties());
            GTEU_CELL_1k = new ItemGTEUCell(CORE_1k, 1, 0.5);
            GTEU_CELL_4k = new ItemGTEUCell(CORE_4k, 4, 1.0);
            GTEU_CELL_16k = new ItemGTEUCell(CORE_16k, 16, 1.5);
            GTEU_CELL_64k = new ItemGTEUCell(CORE_64k, 64, 2.0);
            GTEU_CELL_256k = new ItemGTEUCell(CORE_256k, 256, 2.5);
            regHandler.item("gteu_cell_housing", GTEU_HOUSING);
            regHandler.item("gteu_1k_cell", GTEU_CELL_1k);
            regHandler.item("gteu_4k_cell", GTEU_CELL_4k);
            regHandler.item("gteu_16k_cell", GTEU_CELL_16k);
            regHandler.item("gteu_64k_cell", GTEU_CELL_64k);
            regHandler.item("gteu_256k_cell", GTEU_CELL_256k);
        }
    }

}
