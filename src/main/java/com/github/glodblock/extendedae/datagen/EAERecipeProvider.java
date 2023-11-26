package com.github.glodblock.extendedae.datagen;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.AEParts;
import appeng.datagen.providers.tags.ConventionTags;
import com.github.glodblock.extendedae.EAE;
import com.github.glodblock.extendedae.common.EAEItemAndBlock;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;

import java.util.function.Consumer;

public class EAERecipeProvider extends FabricRecipeProvider {

    static String C = "has_item";

    public EAERecipeProvider(FabricDataOutput p) {
        super(p);
    }

    @Override
    public void buildRecipes(Consumer<FinishedRecipe> c) {
        // Extended Parttern Provider
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EAEItemAndBlock.EX_PATTERN_PROVIDER)
                .pattern("PC")
                .pattern("CZ")
                .define('P', ConventionTags.PATTERN_PROVIDER)
                .define('C', AEItems.CAPACITY_CARD)
                .define('Z', AEItems.ENGINEERING_PROCESSOR)
                .unlockedBy(C, has(EAEItemAndBlock.EX_PATTERN_PROVIDER))
                .save(c, EAE.id("epp"));
        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.MISC, EAEItemAndBlock.EX_PATTERN_PROVIDER_PART)
                .requires(EAEItemAndBlock.EX_PATTERN_PROVIDER)
                .unlockedBy(C, has(EAEItemAndBlock.EX_PATTERN_PROVIDER_PART))
                .save(c, EAE.id("epp_part"));
        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.MISC, EAEItemAndBlock.EX_PATTERN_PROVIDER)
                .requires(EAEItemAndBlock.EX_PATTERN_PROVIDER_PART)
                .unlockedBy(C, has(EAEItemAndBlock.EX_PATTERN_PROVIDER))
                .save(c, EAE.id("epp_alt"));
        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.MISC, EAEItemAndBlock.PATTERN_PROVIDER_UPGRADE)
                .requires(AEItems.CAPACITY_CARD, 2)
                .requires(AEItems.ENGINEERING_PROCESSOR)
                .unlockedBy(C, has(EAEItemAndBlock.PATTERN_PROVIDER_UPGRADE))
                .save(c, EAE.id("epp_upgrade"));

        // Extended Interface
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EAEItemAndBlock.EX_INTERFACE)
                .pattern("PC")
                .pattern("CZ")
                .define('P', ConventionTags.INTERFACE)
                .define('C', AEItems.CAPACITY_CARD)
                .define('Z', AEItems.LOGIC_PROCESSOR)
                .unlockedBy(C, has(EAEItemAndBlock.EX_INTERFACE))
                .save(c, EAE.id("ei"));
        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.MISC, EAEItemAndBlock.EX_INTERFACE_PART)
                .requires(EAEItemAndBlock.EX_INTERFACE)
                .unlockedBy(C, has(EAEItemAndBlock.EX_INTERFACE_PART))
                .save(c, EAE.id("ei_part"));
        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.MISC, EAEItemAndBlock.EX_INTERFACE)
                .requires(EAEItemAndBlock.EX_INTERFACE_PART)
                .unlockedBy(C, has(EAEItemAndBlock.EX_INTERFACE))
                .save(c, EAE.id("ei_alt"));
        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.MISC, EAEItemAndBlock.INTERFACE_UPGRADE)
                .requires(AEItems.CAPACITY_CARD, 2)
                .requires(AEItems.LOGIC_PROCESSOR)
                .unlockedBy(C, has(EAEItemAndBlock.INTERFACE_UPGRADE))
                .save(c, EAE.id("ei_upgrade"));

        // Infinity Cell
        NBTRecipeBuilder
                .shaped(RecipeCategory.MISC, EAEItemAndBlock.INFINITY_CELL.getRecordCell(AEFluidKey.of(Fluids.WATER)))
                .pattern("CWC")
                .pattern("WXW")
                .pattern("III")
                .define('C', AEBlocks.QUARTZ_GLASS)
                .define('W', Items.WATER_BUCKET)
                .define('X', AEItems.CELL_COMPONENT_16K)
                .define('I', ConventionTags.DIAMOND)
                .unlockedBy(C, has(EAEItemAndBlock.INFINITY_CELL))
                .save(c, EAE.id("water_cell"));
        NBTRecipeBuilder
                .shaped(RecipeCategory.MISC, EAEItemAndBlock.INFINITY_CELL.getRecordCell(AEItemKey.of(Blocks.COBBLESTONE)))
                .pattern("CLC")
                .pattern("WXW")
                .pattern("III")
                .define('C', AEBlocks.QUARTZ_GLASS)
                .define('L', Items.LAVA_BUCKET)
                .define('W', Items.WATER_BUCKET)
                .define('X', AEItems.CELL_COMPONENT_16K)
                .define('I', ConventionTags.DIAMOND)
                .unlockedBy(C, has(EAEItemAndBlock.INFINITY_CELL))
                .save(c, EAE.id("cobblestone_cell"));

        // Extended IO Bus
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EAEItemAndBlock.EX_EXPORT_BUS)
                .pattern("PS")
                .pattern("SZ")
                .define('P', AEParts.EXPORT_BUS)
                .define('S', AEItems.SPEED_CARD)
                .define('Z', AEItems.CALCULATION_PROCESSOR)
                .unlockedBy(C, has(EAEItemAndBlock.EX_EXPORT_BUS))
                .save(c, EAE.id("ebus_out"));
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EAEItemAndBlock.EX_IMPORT_BUS)
                .pattern("PS")
                .pattern("SZ")
                .define('P', AEParts.IMPORT_BUS)
                .define('S', AEItems.SPEED_CARD)
                .define('Z', AEItems.CALCULATION_PROCESSOR)
                .unlockedBy(C, has(EAEItemAndBlock.EX_IMPORT_BUS))
                .save(c, EAE.id("ebus_in"));
        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.MISC, EAEItemAndBlock.IO_BUS_UPGRADE)
                .requires(AEItems.SPEED_CARD, 2)
                .requires(AEItems.CALCULATION_PROCESSOR)
                .unlockedBy(C, has(EAEItemAndBlock.IO_BUS_UPGRADE))
                .save(c, EAE.id("ebus_upgrade"));

        // Extended Pattern Access Terminal
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EAEItemAndBlock.EX_PATTERN_TERMINAL)
                .pattern(" L ")
                .pattern("CPC")
                .pattern(" C ")
                .define('L', Blocks.REDSTONE_LAMP)
                .define('P', AEParts.PATTERN_ACCESS_TERMINAL)
                .define('C', AEItems.LOGIC_PROCESSOR)
                .unlockedBy(C, has(EAEItemAndBlock.EX_PATTERN_TERMINAL))
                .save(c, EAE.id("epa"));
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EAEItemAndBlock.PATTERN_UPGRADE)
                .pattern(" L ")
                .pattern("CCC")
                .define('L', Blocks.REDSTONE_LAMP)
                .define('C', AEItems.LOGIC_PROCESSOR)
                .unlockedBy(C, has(EAEItemAndBlock.PATTERN_UPGRADE))
                .save(c, EAE.id("epa_upgrade"));

        // ME Packing Tape
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EAEItemAndBlock.PACKING_TAPE)
                .pattern("FG ")
                .pattern("PIP")
                .pattern(" GF")
                .define('I', ConventionTags.IRON_INGOT)
                .define('P', Items.PAPER)
                .define('G', Items.SLIME_BALL)
                .define('F', ConventionTags.FLUIX_DUST)
                .unlockedBy(C, has(EAEItemAndBlock.PACKING_TAPE))
                .save(c, EAE.id("tape"));

        // Wireless Connector
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EAEItemAndBlock.WIRELESS_CONNECTOR, 2)
                .pattern("DWD")
                .pattern("CEC")
                .pattern("DWD")
                .define('D', AEItems.SKY_DUST)
                .define('W', AEItems.WIRELESS_RECEIVER)
                .define('C', ConventionTags.SMART_CABLE)
                .define('E', AEItems.ENGINEERING_PROCESSOR)
                .unlockedBy(C, has(EAEItemAndBlock.WIRELESS_CONNECTOR))
                .save(c, EAE.id("wireless_connector"));

        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EAEItemAndBlock.WIRELESS_TOOL)
                .pattern(" W ")
                .pattern("ICI")
                .pattern(" I ")
                .define('I', ConventionTags.IRON_INGOT)
                .define('W', AEItems.WIRELESS_RECEIVER)
                .define('C', AEItems.CALCULATION_PROCESSOR)
                .unlockedBy(C, has(EAEItemAndBlock.WIRELESS_TOOL))
                .save(c, EAE.id("wireless_tool"));

        // Ingredient Buffer
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EAEItemAndBlock.INGREDIENT_BUFFER)
                .pattern("IKI")
                .pattern("G G")
                .pattern("IKI")
                .define('I', ConventionTags.IRON_INGOT)
                .define('K', AEItems.CELL_COMPONENT_1K)
                .define('G', AEBlocks.QUARTZ_GLASS)
                .unlockedBy(C, has(EAEItemAndBlock.INGREDIENT_BUFFER))
                .save(c, EAE.id("ingredient_buffer"));

        // Extended ME Drive
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EAEItemAndBlock.EX_DRIVE)
                .pattern("CDC")
                .pattern("FEF")
                .define('C', ConventionTags.GLASS_CABLE)
                .define('D', AEBlocks.DRIVE)
                .define('F', ConventionTags.FLUIX_DUST)
                .define('E', AEItems.CAPACITY_CARD)
                .unlockedBy(C, has(EAEItemAndBlock.EX_DRIVE))
                .save(c, EAE.id("ex_drive"));

        // Drive Upgrade
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EAEItemAndBlock.DRIVE_UPGRADE)
                .pattern("C C")
                .pattern("FEF")
                .define('C', ConventionTags.GLASS_CABLE)
                .define('F', ConventionTags.FLUIX_DUST)
                .define('E', AEItems.CAPACITY_CARD)
                .unlockedBy(C, has(EAEItemAndBlock.DRIVE_UPGRADE))
                .save(c, EAE.id("ex_drive_upgrade"));

        // Pattern Modifier
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EAEItemAndBlock.PATTERN_MODIFIER)
                .pattern("GPG")
                .pattern(" L ")
                .define('G', ConventionTags.dye(DyeColor.GREEN))
                .define('P', AEItems.BLANK_PATTERN)
                .define('L', AEItems.LOGIC_PROCESSOR)
                .unlockedBy(C, has(EAEItemAndBlock.PATTERN_MODIFIER))
                .save(c, EAE.id("pattern_modifier"));

        // Extended Molecular Assembler
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EAEItemAndBlock.EX_ASSEMBLER)
                .pattern("FAF")
                .pattern("AEA")
                .pattern("FAF")
                .define('F', ConventionTags.FLUIX_CRYSTAL)
                .define('A', AEBlocks.MOLECULAR_ASSEMBLER)
                .define('E', AEItems.ENGINEERING_PROCESSOR)
                .unlockedBy(C, has(EAEItemAndBlock.EX_ASSEMBLER))
                .save(c, EAE.id("ex_molecular_assembler"));

        // Extended Inscriber
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EAEItemAndBlock.EX_INSCRIBER)
                .pattern("FAF")
                .pattern("AEA")
                .pattern("FAF")
                .define('F', AEBlocks.INSCRIBER)
                .define('A', AEParts.STORAGE_BUS)
                .define('E', ConventionTags.INTERFACE)
                .unlockedBy(C, has(EAEItemAndBlock.EX_INSCRIBER))
                .save(c, EAE.id("ex_inscriber"));

        // Extended Charger
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EAEItemAndBlock.EX_CHARGER)
                .pattern("FAF")
                .pattern("AEA")
                .pattern("FAF")
                .define('F', AEBlocks.CHARGER)
                .define('A', AEParts.STORAGE_BUS)
                .define('E', ConventionTags.INTERFACE)
                .unlockedBy(C, has(EAEItemAndBlock.EX_CHARGER))
                .save(c, EAE.id("ex_charger"));
    }

}
