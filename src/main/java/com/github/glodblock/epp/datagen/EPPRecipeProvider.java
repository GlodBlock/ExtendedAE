package com.github.glodblock.epp.datagen;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.AEParts;
import appeng.datagen.providers.tags.ConventionTags;
import com.github.glodblock.epp.EPP;
import com.github.glodblock.epp.common.EPPItemAndBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class EPPRecipeProvider extends RecipeProvider {

    static String C = "has_item";

    public EPPRecipeProvider(DataGenerator p) {
        super(p);
    }

    @Override
    protected void buildCraftingRecipes(@NotNull Consumer<FinishedRecipe> c) {
        // Extended Parttern Provider
        ShapedRecipeBuilder
                .shaped(EPPItemAndBlock.EX_PATTERN_PROVIDER)
                .pattern("PC")
                .pattern("CZ")
                .define('P', ConventionTags.PATTERN_PROVIDER)
                .define('C', AEItems.CAPACITY_CARD)
                .define('Z', AEItems.ENGINEERING_PROCESSOR)
                .unlockedBy(C, has(EPPItemAndBlock.EX_PATTERN_PROVIDER))
                .save(c, EPP.id("epp"));
        ShapelessRecipeBuilder
                .shapeless(EPPItemAndBlock.EX_PATTERN_PROVIDER_PART)
                .requires(EPPItemAndBlock.EX_PATTERN_PROVIDER)
                .unlockedBy(C, has(EPPItemAndBlock.EX_PATTERN_PROVIDER_PART))
                .save(c, EPP.id("epp_part"));
        ShapelessRecipeBuilder
                .shapeless(EPPItemAndBlock.EX_PATTERN_PROVIDER)
                .requires(EPPItemAndBlock.EX_PATTERN_PROVIDER_PART)
                .unlockedBy(C, has(EPPItemAndBlock.EX_PATTERN_PROVIDER))
                .save(c, EPP.id("epp_alt"));
        ShapelessRecipeBuilder
                .shapeless(EPPItemAndBlock.PATTERN_PROVIDER_UPGRADE)
                .requires(AEItems.CAPACITY_CARD, 2)
                .requires(AEItems.ENGINEERING_PROCESSOR)
                .unlockedBy(C, has(EPPItemAndBlock.PATTERN_PROVIDER_UPGRADE))
                .save(c, EPP.id("epp_upgrade"));

        // Extended Interface
        ShapedRecipeBuilder
                .shaped(EPPItemAndBlock.EX_INTERFACE)
                .pattern("PC")
                .pattern("CZ")
                .define('P', ConventionTags.INTERFACE)
                .define('C', AEItems.CAPACITY_CARD)
                .define('Z', AEItems.LOGIC_PROCESSOR)
                .unlockedBy(C, has(EPPItemAndBlock.EX_INTERFACE))
                .save(c, EPP.id("ei"));
        ShapelessRecipeBuilder
                .shapeless(EPPItemAndBlock.EX_INTERFACE_PART)
                .requires(EPPItemAndBlock.EX_INTERFACE)
                .unlockedBy(C, has(EPPItemAndBlock.EX_INTERFACE_PART))
                .save(c, EPP.id("ei_part"));
        ShapelessRecipeBuilder
                .shapeless(EPPItemAndBlock.EX_INTERFACE)
                .requires(EPPItemAndBlock.EX_INTERFACE_PART)
                .unlockedBy(C, has(EPPItemAndBlock.EX_INTERFACE))
                .save(c, EPP.id("ei_alt"));
        ShapelessRecipeBuilder
                .shapeless(EPPItemAndBlock.INTERFACE_UPGRADE)
                .requires(AEItems.CAPACITY_CARD, 2)
                .requires(AEItems.LOGIC_PROCESSOR)
                .unlockedBy(C, has(EPPItemAndBlock.INTERFACE_UPGRADE))
                .save(c, EPP.id("ei_upgrade"));

        // Infinity Cell
        NBTRecipeBuilder
                .shaped(EPPItemAndBlock.INFINITY_CELL.getRecordCell(AEFluidKey.of(Fluids.WATER)))
                .pattern("CWC")
                .pattern("WXW")
                .pattern("III")
                .define('C', AEBlocks.QUARTZ_GLASS)
                .define('W', Items.WATER_BUCKET)
                .define('X', AEItems.CELL_COMPONENT_16K)
                .define('I', ConventionTags.DIAMOND)
                .unlockedBy(C, has(EPPItemAndBlock.INFINITY_CELL))
                .save(c, EPP.id("water_cell"));
        NBTRecipeBuilder
                .shaped(EPPItemAndBlock.INFINITY_CELL.getRecordCell(AEItemKey.of(Blocks.COBBLESTONE)))
                .pattern("CLC")
                .pattern("WXW")
                .pattern("III")
                .define('C', AEBlocks.QUARTZ_GLASS)
                .define('L', Items.LAVA_BUCKET)
                .define('W', Items.WATER_BUCKET)
                .define('X', AEItems.CELL_COMPONENT_16K)
                .define('I', ConventionTags.DIAMOND)
                .unlockedBy(C, has(EPPItemAndBlock.INFINITY_CELL))
                .save(c, EPP.id("cobblestone_cell"));

        // Extended IO Bus
        ShapedRecipeBuilder
                .shaped(EPPItemAndBlock.EX_EXPORT_BUS)
                .pattern("PS")
                .pattern("SZ")
                .define('P', AEParts.EXPORT_BUS)
                .define('S', AEItems.SPEED_CARD)
                .define('Z', AEItems.CALCULATION_PROCESSOR)
                .unlockedBy(C, has(EPPItemAndBlock.EX_EXPORT_BUS))
                .save(c, EPP.id("ebus_out"));
        ShapedRecipeBuilder
                .shaped(EPPItemAndBlock.EX_IMPORT_BUS)
                .pattern("PS")
                .pattern("SZ")
                .define('P', AEParts.IMPORT_BUS)
                .define('S', AEItems.SPEED_CARD)
                .define('Z', AEItems.CALCULATION_PROCESSOR)
                .unlockedBy(C, has(EPPItemAndBlock.EX_IMPORT_BUS))
                .save(c, EPP.id("ebus_in"));
        ShapelessRecipeBuilder
                .shapeless(EPPItemAndBlock.IO_BUS_UPGRADE)
                .requires(AEItems.SPEED_CARD, 2)
                .requires(AEItems.CALCULATION_PROCESSOR)
                .unlockedBy(C, has(EPPItemAndBlock.IO_BUS_UPGRADE))
                .save(c, EPP.id("ebus_upgrade"));

        // ME Packing Tape
        ShapedRecipeBuilder
                .shaped(EPPItemAndBlock.PACKING_TAPE)
                .pattern("FG ")
                .pattern("PIP")
                .pattern(" GF")
                .define('I', ConventionTags.IRON_INGOT)
                .define('P', Items.PAPER)
                .define('G', Items.SLIME_BALL)
                .define('F', ConventionTags.FLUIX_DUST)
                .unlockedBy(C, has(EPPItemAndBlock.PACKING_TAPE))
                .save(c, EPP.id("tape"));

        // Extended ME Drive
        ShapedRecipeBuilder
                .shaped(EPPItemAndBlock.EX_DRIVE)
                .pattern("CDC")
                .pattern("FEF")
                .define('C', ConventionTags.GLASS_CABLE)
                .define('D', AEBlocks.DRIVE)
                .define('F', ConventionTags.FLUIX_DUST)
                .define('E', AEItems.CAPACITY_CARD)
                .unlockedBy(C, has(EPPItemAndBlock.EX_DRIVE))
                .save(c, EPP.id("ex_drive"));

        // Drive Upgrade
        ShapedRecipeBuilder
                .shaped(EPPItemAndBlock.DRIVE_UPGRADE)
                .pattern("C C")
                .pattern("FEF")
                .define('C', ConventionTags.GLASS_CABLE)
                .define('F', ConventionTags.FLUIX_DUST)
                .define('E', AEItems.CAPACITY_CARD)
                .unlockedBy(C, has(EPPItemAndBlock.DRIVE_UPGRADE))
                .save(c, EPP.id("ex_drive_upgrade"));

        // Ingredient Buffer
        ShapedRecipeBuilder
                .shaped(EPPItemAndBlock.INGREDIENT_BUFFER)
                .pattern("IKI")
                .pattern("G G")
                .pattern("IKI")
                .define('I', ConventionTags.IRON_INGOT)
                .define('K', AEItems.CELL_COMPONENT_1K)
                .define('G', AEBlocks.QUARTZ_GLASS)
                .unlockedBy(C, has(EPPItemAndBlock.INGREDIENT_BUFFER))
                .save(c, EPP.id("ingredient_buffer"));

    }
}
