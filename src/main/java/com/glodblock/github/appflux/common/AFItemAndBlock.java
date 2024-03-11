package com.glodblock.github.appflux.common;

import appeng.items.parts.PartItem;
import com.glodblock.github.appflux.common.blocks.BlockFluxAccessor;
import com.glodblock.github.appflux.common.items.ItemFECell;
import com.glodblock.github.appflux.common.items.NormalItem;
import com.glodblock.github.appflux.common.parts.PartFluxAccessor;
import com.glodblock.github.appflux.common.tileentities.TileFluxAccessor;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AFItemAndBlock {

    public static NormalItem CORE_1k;
    public static NormalItem CORE_4k;
    public static NormalItem CORE_16k;
    public static NormalItem CORE_64k;
    public static NormalItem CORE_256k;
    public static NormalItem CHARGED_REDSTONE;
    public static NormalItem ENERGY_PROCESSOR_PRINT;
    public static NormalItem ENERGY_PROCESSOR_PRESS;
    public static NormalItem ENERGY_PROCESSOR;
    public static NormalItem REDSTONE_CRYSTAL;
    public static NormalItem INSULATING_RESIN;
    public static NormalItem HARDEN_INSULATING_RESIN;
    public static NormalItem FE_HOUSING;
    public static ItemFECell FE_CELL_1k;
    public static ItemFECell FE_CELL_4k;
    public static ItemFECell FE_CELL_16k;
    public static ItemFECell FE_CELL_64k;
    public static ItemFECell FE_CELL_256k;
    public static NormalItem GTEU_HOUSING;
    public static BlockFluxAccessor FLUX_ACCESSOR;
    public static PartItem<PartFluxAccessor> PART_FLUX_ACCESSOR;

    public static void init(AFRegistryHandler regHandler) {
        CORE_1k = new NormalItem();
        CORE_4k = new NormalItem();
        CORE_16k = new NormalItem();
        CORE_64k = new NormalItem();
        CORE_256k = new NormalItem();
        CHARGED_REDSTONE = new NormalItem();
        REDSTONE_CRYSTAL = new NormalItem();
        INSULATING_RESIN = new NormalItem();
        HARDEN_INSULATING_RESIN = new NormalItem();
        ENERGY_PROCESSOR = new NormalItem();
        ENERGY_PROCESSOR_PRINT = new NormalItem();
        ENERGY_PROCESSOR_PRESS = new NormalItem();
        FE_HOUSING = new NormalItem();
        FE_CELL_1k = new ItemFECell(CORE_1k, 1, 0.5);
        FE_CELL_4k = new ItemFECell(CORE_4k, 4, 1.0);
        FE_CELL_16k = new ItemFECell(CORE_16k, 16, 1.5);
        FE_CELL_64k = new ItemFECell(CORE_64k, 64, 2.0);
        FE_CELL_256k = new ItemFECell(CORE_256k, 256, 2.5);
        FLUX_ACCESSOR = new BlockFluxAccessor();
        PART_FLUX_ACCESSOR = new PartItem<>(new Item.Properties(), PartFluxAccessor.class, PartFluxAccessor::new) {
            @Override
            public void appendHoverText(@NotNull ItemStack stack, Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag advancedTooltips) {
                tooltip.add(Component.translatable("block.appflux.flux_accessor.tooltip.1").withStyle(ChatFormatting.GRAY));
                tooltip.add(Component.translatable("block.appflux.flux_accessor.tooltip.2").withStyle(ChatFormatting.GRAY));
            }
        };
        regHandler.item("core_1k", CORE_1k);
        regHandler.item("core_4k", CORE_4k);
        regHandler.item("core_16k", CORE_16k);
        regHandler.item("core_64k", CORE_64k);
        regHandler.item("core_256k", CORE_256k);
        regHandler.item("charged_redstone", CHARGED_REDSTONE);
        regHandler.item("redstone_crystal", REDSTONE_CRYSTAL);
        regHandler.item("insulating_resin", INSULATING_RESIN);
        regHandler.item("harden_insulating_resin", HARDEN_INSULATING_RESIN);
        regHandler.item("energy_processor", ENERGY_PROCESSOR);
        regHandler.item("printed_energy_processor", ENERGY_PROCESSOR_PRINT);
        regHandler.item("energy_processor_press", ENERGY_PROCESSOR_PRESS);
        regHandler.item("fe_cell_housing", FE_HOUSING);
        regHandler.item("fe_1k_cell", FE_CELL_1k);
        regHandler.item("fe_4k_cell", FE_CELL_4k);
        regHandler.item("fe_16k_cell", FE_CELL_16k);
        regHandler.item("fe_64k_cell", FE_CELL_64k);
        regHandler.item("fe_256k_cell", FE_CELL_256k);
        regHandler.item("part_flux_accessor", PART_FLUX_ACCESSOR);
        regHandler.block("flux_accessor", FLUX_ACCESSOR, TileFluxAccessor.class, TileFluxAccessor::new);
    }

}
