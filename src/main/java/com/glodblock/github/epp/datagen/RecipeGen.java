package com.glodblock.github.epp.datagen;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.AEParts;
import appeng.datagen.providers.tags.ConventionTags;
import com.glodblock.github.epp.EPP;
import com.glodblock.github.epp.common.EPPItemAndBlock;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Items;

import java.util.function.Consumer;

public class RecipeGen extends FabricRecipeProvider {

    public RecipeGen(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    protected void generateRecipes(Consumer<RecipeJsonProvider> exporter) {
        // Extended Parttern Provider
        ShapedRecipeJsonBuilder
                .create(EPPItemAndBlock.EX_PATTERN_PROVIDER)
                .pattern("PC")
                .pattern("CZ")
                .input('P', AEBlocks.PATTERN_PROVIDER)
                .input('C', AEItems.CAPACITY_CARD)
                .input('Z', AEItems.ENGINEERING_PROCESSOR)
                .criterion(FabricRecipeProvider.hasItem(EPPItemAndBlock.EX_PATTERN_PROVIDER), FabricRecipeProvider.conditionsFromItem(EPPItemAndBlock.EX_PATTERN_PROVIDER))
                .offerTo(exporter, EPP.id("epp"));
        ShapelessRecipeJsonBuilder
                .create(EPPItemAndBlock.EX_PATTERN_PROVIDER_PART)
                .input(EPPItemAndBlock.EX_PATTERN_PROVIDER)
                .criterion(FabricRecipeProvider.hasItem(EPPItemAndBlock.EX_PATTERN_PROVIDER_PART), FabricRecipeProvider.conditionsFromItem(EPPItemAndBlock.EX_PATTERN_PROVIDER_PART))
                .offerTo(exporter, EPP.id("epp_part"));
        ShapelessRecipeJsonBuilder
                .create(EPPItemAndBlock.EX_PATTERN_PROVIDER)
                .input(EPPItemAndBlock.EX_PATTERN_PROVIDER_PART)
                .criterion(FabricRecipeProvider.hasItem(EPPItemAndBlock.EX_PATTERN_PROVIDER), FabricRecipeProvider.conditionsFromItem(EPPItemAndBlock.EX_PATTERN_PROVIDER))
                .offerTo(exporter, EPP.id("epp_alt"));
        ShapelessRecipeJsonBuilder
                .create(EPPItemAndBlock.PATTERN_PROVIDER_UPGRADE)
                .input(AEItems.CAPACITY_CARD, 2)
                .input(AEItems.ENGINEERING_PROCESSOR)
                .criterion(FabricRecipeProvider.hasItem(EPPItemAndBlock.PATTERN_PROVIDER_UPGRADE), FabricRecipeProvider.conditionsFromItem(EPPItemAndBlock.PATTERN_PROVIDER_UPGRADE))
                .offerTo(exporter, EPP.id("epp_upgrade"));

        // Extended Interface
        ShapedRecipeJsonBuilder
                .create(EPPItemAndBlock.EX_INTERFACE)
                .pattern("PC")
                .pattern("CZ")
                .input('P', ConventionTags.INTERFACE)
                .input('C', AEItems.CAPACITY_CARD)
                .input('Z', AEItems.LOGIC_PROCESSOR)
                .criterion(FabricRecipeProvider.hasItem(EPPItemAndBlock.EX_INTERFACE), FabricRecipeProvider.conditionsFromItem(EPPItemAndBlock.EX_INTERFACE))
                .offerTo(exporter, EPP.id("ei"));
        ShapelessRecipeJsonBuilder
                .create(EPPItemAndBlock.EX_INTERFACE_PART)
                .input(EPPItemAndBlock.EX_INTERFACE)
                .criterion(FabricRecipeProvider.hasItem(EPPItemAndBlock.EX_INTERFACE_PART), FabricRecipeProvider.conditionsFromItem(EPPItemAndBlock.EX_INTERFACE_PART))
                .offerTo(exporter, EPP.id("ei_part"));
        ShapelessRecipeJsonBuilder
                .create(EPPItemAndBlock.EX_INTERFACE)
                .input(EPPItemAndBlock.EX_INTERFACE_PART)
                .criterion(FabricRecipeProvider.hasItem(EPPItemAndBlock.EX_INTERFACE), FabricRecipeProvider.conditionsFromItem(EPPItemAndBlock.EX_INTERFACE))
                .offerTo(exporter, EPP.id("ei_alt"));
        ShapelessRecipeJsonBuilder
                .create(EPPItemAndBlock.INTERFACE_UPGRADE)
                .input(AEItems.CAPACITY_CARD, 2)
                .input(AEItems.LOGIC_PROCESSOR)
                .criterion(FabricRecipeProvider.hasItem(EPPItemAndBlock.INTERFACE_UPGRADE), FabricRecipeProvider.conditionsFromItem(EPPItemAndBlock.INTERFACE_UPGRADE))
                .offerTo(exporter, EPP.id("ei_upgrade"));

        // Infinity Cell
        NBTRecipeBuilder
                .create(EPPItemAndBlock.INFINITY_CELL.getRecordCell(AEFluidKey.of(Fluids.WATER)))
                .pattern("CWC")
                .pattern("WXW")
                .pattern("III")
                .input('C', AEBlocks.QUARTZ_GLASS)
                .input('W', Items.WATER_BUCKET)
                .input('X', AEItems.CELL_COMPONENT_16K)
                .input('I', ConventionTags.DIAMOND)
                .criterion(FabricRecipeProvider.hasItem(EPPItemAndBlock.INFINITY_CELL), FabricRecipeProvider.conditionsFromItem(EPPItemAndBlock.INFINITY_CELL))
                .offerTo(exporter, EPP.id("water_cell"));
        NBTRecipeBuilder
                .create(EPPItemAndBlock.INFINITY_CELL.getRecordCell(AEItemKey.of(Blocks.COBBLESTONE)))
                .pattern("CLC")
                .pattern("WXW")
                .pattern("III")
                .input('C', AEBlocks.QUARTZ_GLASS)
                .input('L', Items.LAVA_BUCKET)
                .input('W', Items.WATER_BUCKET)
                .input('X', AEItems.CELL_COMPONENT_16K)
                .input('I', ConventionTags.DIAMOND)
                .criterion(FabricRecipeProvider.hasItem(EPPItemAndBlock.INFINITY_CELL), FabricRecipeProvider.conditionsFromItem(EPPItemAndBlock.INFINITY_CELL))
                .offerTo(exporter, EPP.id("cobblestone_cell"));

        // Extended IO Bus
        ShapedRecipeJsonBuilder
                .create(EPPItemAndBlock.EX_EXPORT_BUS)
                .pattern("PS")
                .pattern("SZ")
                .input('P', AEParts.EXPORT_BUS)
                .input('S', AEItems.SPEED_CARD)
                .input('Z', AEItems.CALCULATION_PROCESSOR)
                .criterion(FabricRecipeProvider.hasItem(EPPItemAndBlock.EX_EXPORT_BUS), FabricRecipeProvider.conditionsFromItem(EPPItemAndBlock.EX_EXPORT_BUS))
                .offerTo(exporter, EPP.id("ebus_out"));
        ShapedRecipeJsonBuilder
                .create(EPPItemAndBlock.EX_IMPORT_BUS)
                .pattern("PS")
                .pattern("SZ")
                .input('P', AEParts.IMPORT_BUS)
                .input('S', AEItems.SPEED_CARD)
                .input('Z', AEItems.CALCULATION_PROCESSOR)
                .criterion(FabricRecipeProvider.hasItem(EPPItemAndBlock.EX_IMPORT_BUS), FabricRecipeProvider.conditionsFromItem(EPPItemAndBlock.EX_IMPORT_BUS))
                .offerTo(exporter, EPP.id("ebus_in"));
        ShapelessRecipeJsonBuilder
                .create(EPPItemAndBlock.IO_BUS_UPGRADE)
                .input(AEItems.SPEED_CARD, 2)
                .input(AEItems.CALCULATION_PROCESSOR)
                .criterion(FabricRecipeProvider.hasItem(EPPItemAndBlock.IO_BUS_UPGRADE), FabricRecipeProvider.conditionsFromItem(EPPItemAndBlock.IO_BUS_UPGRADE))
                .offerTo(exporter, EPP.id("ebus_upgrade"));

        // ME Packing Tape
        ShapedRecipeJsonBuilder
                .create(EPPItemAndBlock.PACKING_TAPE)
                .pattern("FG ")
                .pattern("PIP")
                .pattern(" GF")
                .input('I', ConventionTags.IRON_INGOT)
                .input('P', Items.PAPER)
                .input('G', Items.SLIME_BALL)
                .input('F', ConventionTags.FLUIX_DUST)
                .criterion(FabricRecipeProvider.hasItem(EPPItemAndBlock.PACKING_TAPE), FabricRecipeProvider.conditionsFromItem(EPPItemAndBlock.PACKING_TAPE))
                .offerTo(exporter, EPP.id("tape"));

        // Wireless Connector
        ShapedRecipeJsonBuilder
                .create(EPPItemAndBlock.WIRELESS_CONNECTOR, 2)
                .pattern("DWD")
                .pattern("CEC")
                .pattern("DWD")
                .input('D', AEItems.SKY_DUST)
                .input('W', AEItems.WIRELESS_RECEIVER)
                .input('C', ConventionTags.SMART_CABLE)
                .input('E', AEItems.ENGINEERING_PROCESSOR)
                .criterion(FabricRecipeProvider.hasItem(EPPItemAndBlock.WIRELESS_CONNECTOR), FabricRecipeProvider.conditionsFromItem(EPPItemAndBlock.WIRELESS_CONNECTOR))
                .offerTo(exporter, EPP.id("wireless_connector"));

        ShapedRecipeJsonBuilder
                .create(EPPItemAndBlock.WIRELESS_TOOL)
                .pattern(" W ")
                .pattern("ICI")
                .pattern(" I ")
                .input('I', ConventionTags.IRON_INGOT)
                .input('W', AEItems.WIRELESS_RECEIVER)
                .input('C', AEItems.CALCULATION_PROCESSOR)
                .criterion(FabricRecipeProvider.hasItem(EPPItemAndBlock.WIRELESS_TOOL), FabricRecipeProvider.conditionsFromItem(EPPItemAndBlock.WIRELESS_TOOL))
                .offerTo(exporter, EPP.id("wireless_tool"));
    }
}
