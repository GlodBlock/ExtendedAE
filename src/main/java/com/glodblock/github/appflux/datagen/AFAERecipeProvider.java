package com.glodblock.github.appflux.datagen;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.datagen.providers.recipes.AE2RecipeProvider;
import appeng.datagen.providers.tags.ConventionTags;
import appeng.recipes.handlers.ChargerRecipeBuilder;
import appeng.recipes.handlers.InscriberProcessType;
import appeng.recipes.handlers.InscriberRecipeBuilder;
import appeng.recipes.transform.TransformCircumstance;
import appeng.recipes.transform.TransformRecipeBuilder;
import com.glodblock.github.appflux.AppFlux;
import com.glodblock.github.appflux.common.AFSingletons;
import com.glodblock.github.appflux.util.AFTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class AFAERecipeProvider extends AE2RecipeProvider {

    static String C = "has_item";

    public AFAERecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput consumer) {
        TransformRecipeBuilder.transform(consumer,
                AppFlux.id("transform/redstone_crystal"),
                AFSingletons.REDSTONE_CRYSTAL, 1,
                TransformCircumstance.fluid(FluidTags.WATER),
                Ingredient.of(Tags.Items.STORAGE_BLOCKS_REDSTONE),
                Ingredient.of(AEBlocks.FLUIX_BLOCK),
                Ingredient.of(AFTags.DIAMOND_DUST)
        );
        ChargerRecipeBuilder.charge(consumer,
                AppFlux.id("charger/energy_press"),
                Blocks.IRON_BLOCK,
                AFSingletons.ENERGY_PROCESSOR_PRESS
        );
        InscriberRecipeBuilder
                .inscribe(AFSingletons.CHARGED_REDSTONE, AFSingletons.ENERGY_PROCESSOR_PRINT, 1)
                .setTop(Ingredient.of(AFSingletons.ENERGY_PROCESSOR_PRESS))
                .setMode(InscriberProcessType.INSCRIBE)
                .save(consumer, AppFlux.id("inscriber/energy_print"));
        InscriberRecipeBuilder
                .inscribe(Items.REDSTONE, AFSingletons.ENERGY_PROCESSOR, 1)
                .setTop(Ingredient.of(AFSingletons.ENERGY_PROCESSOR_PRINT))
                .setBottom(Ingredient.of(AEItems.SILICON_PRINT))
                .setMode(InscriberProcessType.PRESS)
                .save(consumer, AppFlux.id("inscriber/energy"));
        InscriberRecipeBuilder
                .inscribe(Items.IRON_BLOCK, AFSingletons.ENERGY_PROCESSOR_PRESS, 1)
                .setTop(Ingredient.of(AFSingletons.ENERGY_PROCESSOR_PRESS))
                .setMode(InscriberProcessType.INSCRIBE)
                .save(consumer, AppFlux.id("inscriber/energy_press"));
        InscriberRecipeBuilder
                .inscribe(ConventionTags.DIAMOND, AFSingletons.DIAMOND_DUST, 1)
                .setMode(InscriberProcessType.INSCRIBE)
                .save(consumer, AppFlux.id("inscriber/crush_diamond"));
        InscriberRecipeBuilder
                .inscribe(Tags.Items.GEMS_EMERALD, AFSingletons.EMERALD_DUST, 1)
                .setMode(InscriberProcessType.INSCRIBE)
                .save(consumer, AppFlux.id("inscriber/crush_emerald"));
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, AFSingletons.CHARGED_REDSTONE)
                .pattern("XYX")
                .pattern("ZCZ")
                .pattern("XYX")
                .define('C', AFSingletons.REDSTONE_CRYSTAL)
                .define('X', ConventionTags.GLOWSTONE)
                .define('Y', AFTags.EMERALD_DUST)
                .define('Z', ConventionTags.ENDER_PEARL_DUST)
                .unlockedBy(C, has(AFSingletons.REDSTONE_CRYSTAL))
                .save(consumer, AppFlux.id("charge_redstone"));
        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.MISC, AFSingletons.INSULATING_RESIN)
                .requires(Items.WATER_BUCKET)
                .requires(Blocks.CACTUS)
                .requires(Blocks.CACTUS)
                .requires(Items.BONE_MEAL)
                .requires(ConventionTags.SILICON)
                .requires(Items.SLIME_BALL)
                .requires(ConventionTags.GLOWSTONE)
                .unlockedBy(C, has(Blocks.CACTUS))
                .save(consumer, AppFlux.id("insulating_resin"));
        SimpleCookingRecipeBuilder
                .smelting(
                        Ingredient.of(AFSingletons.INSULATING_RESIN),
                        RecipeCategory.MISC,
                        AFSingletons.HARDEN_INSULATING_RESIN,
                        0.35F, 200
                )
                .unlockedBy(C, has(AFSingletons.INSULATING_RESIN))
                .save(consumer,  AppFlux.id("smelting/harden_resin"));
        SimpleCookingRecipeBuilder
                .blasting(
                        Ingredient.of(AFSingletons.INSULATING_RESIN),
                        RecipeCategory.MISC,
                        AFSingletons.HARDEN_INSULATING_RESIN,
                        0.35F, 100
                )
                .unlockedBy(C, has(AFSingletons.INSULATING_RESIN))
                .save(consumer,  AppFlux.id("blasting/harden_resin"));
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, AFSingletons.CORE_1k)
                .pattern("DCD")
                .pattern("CPC")
                .pattern("DCD")
                .define('D', ConventionTags.CERTUS_QUARTZ_DUST)
                .define('C', AFSingletons.REDSTONE_CRYSTAL)
                .define('P', AEItems.LOGIC_PROCESSOR)
                .unlockedBy(C, has(AFSingletons.REDSTONE_CRYSTAL))
                .save(consumer, AppFlux.id("1k_core"));
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, AFSingletons.CORE_4k)
                .pattern("DPD")
                .pattern("LGL")
                .pattern("DLD")
                .define('D', ConventionTags.CERTUS_QUARTZ_DUST)
                .define('L', AFSingletons.CORE_1k)
                .define('G', AEBlocks.QUARTZ_VIBRANT_GLASS)
                .define('P', AFSingletons.ENERGY_PROCESSOR)
                .unlockedBy(C, has(AFSingletons.ENERGY_PROCESSOR))
                .save(consumer, AppFlux.id("4k_core"));
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, AFSingletons.CORE_16k)
                .pattern("DPD")
                .pattern("LGL")
                .pattern("DLD")
                .define('D', AFTags.DIAMOND_DUST)
                .define('L', AFSingletons.CORE_4k)
                .define('G', AEBlocks.QUARTZ_VIBRANT_GLASS)
                .define('P', AFSingletons.ENERGY_PROCESSOR)
                .unlockedBy(C, has(AFSingletons.ENERGY_PROCESSOR))
                .save(consumer, AppFlux.id("16k_core"));
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, AFSingletons.CORE_64k)
                .pattern("DPD")
                .pattern("LGL")
                .pattern("DLD")
                .define('D', AFTags.DIAMOND_DUST)
                .define('L', AFSingletons.CORE_16k)
                .define('G', AEBlocks.QUARTZ_VIBRANT_GLASS)
                .define('P', AFSingletons.ENERGY_PROCESSOR)
                .unlockedBy(C, has(AFSingletons.ENERGY_PROCESSOR))
                .save(consumer, AppFlux.id("64k_core"));
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, AFSingletons.CORE_256k)
                .pattern("DPD")
                .pattern("LGL")
                .pattern("DLD")
                .define('D', AFTags.EMERALD_DUST)
                .define('L', AFSingletons.CORE_64k)
                .define('G', AEBlocks.QUARTZ_VIBRANT_GLASS)
                .define('P', AFSingletons.ENERGY_PROCESSOR)
                .unlockedBy(C, has(AFSingletons.ENERGY_PROCESSOR))
                .save(consumer, AppFlux.id("256k_core"));
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, AFSingletons.FLUX_ACCESSOR)
                .pattern("DPD")
                .pattern("IMI")
                .pattern("DPD")
                .define('D', ConventionTags.GLOWSTONE)
                .define('I', ConventionTags.COPPER_INGOT)
                .define('M', AEBlocks.ENERGY_ACCEPTOR)
                .define('P', AFSingletons.ENERGY_PROCESSOR)
                .unlockedBy(C, has(AFSingletons.ENERGY_PROCESSOR))
                .save(consumer, AppFlux.id("flux_accessor"));
        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.MISC, AFSingletons.FLUX_ACCESSOR)
                .requires(AFSingletons.PART_FLUX_ACCESSOR)
                .unlockedBy(C, has(AFSingletons.FLUX_ACCESSOR))
                .save(consumer, AppFlux.id("flux_accessor_alt"));
        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.MISC, AFSingletons.PART_FLUX_ACCESSOR)
                .requires(AFSingletons.FLUX_ACCESSOR)
                .unlockedBy(C, has(AFSingletons.PART_FLUX_ACCESSOR))
                .save(consumer, AppFlux.id("part_flux_accessor"));
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, AFSingletons.FE_HOUSING)
                .pattern("GDG")
                .pattern("D D")
                .pattern("III")
                .define('D', ConventionTags.REDSTONE)
                .define('G', AEBlocks.QUARTZ_GLASS)
                .define('I', AFTags.RESIN_INGOT)
                .unlockedBy(C, has(AFSingletons.HARDEN_INSULATING_RESIN))
                .save(consumer, AppFlux.id("fe_housing"));
        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.MISC, AFSingletons.INDUCTION_CARD)
                .requires(AFSingletons.FLUX_ACCESSOR)
                .requires(AEItems.BASIC_CARD)
                .unlockedBy(C, has(AEItems.BASIC_CARD))
                .save(consumer, AppFlux.id("induction_card"));
        addFECellRecipe(consumer, AFSingletons.CORE_1k, AFSingletons.FE_CELL_1k, "1k");
        addFECellRecipe(consumer, AFSingletons.CORE_4k, AFSingletons.FE_CELL_4k, "4k");
        addFECellRecipe(consumer, AFSingletons.CORE_16k, AFSingletons.FE_CELL_16k, "16k");
        addFECellRecipe(consumer, AFSingletons.CORE_64k, AFSingletons.FE_CELL_64k, "64k");
        addFECellRecipe(consumer, AFSingletons.CORE_256k, AFSingletons.FE_CELL_256k, "256k");
    }

    private void addFECellRecipe(RecipeOutput consumer, Item core, Item result, String id) {
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, result)
                .pattern("GDG")
                .pattern("DXD")
                .pattern("III")
                .define('D', ConventionTags.REDSTONE)
                .define('G', AEBlocks.QUARTZ_GLASS)
                .define('I', AFTags.RESIN_INGOT)
                .define('X', core)
                .unlockedBy(C, has(AFSingletons.HARDEN_INSULATING_RESIN))
                .save(consumer, AppFlux.id(id + "_fe_cell"));
        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.MISC, result)
                .requires(AFSingletons.FE_HOUSING)
                .requires(core)
                .unlockedBy(C, has(AFSingletons.FE_HOUSING))
                .save(consumer, AppFlux.id(id + "_fe_cell_assemble"));
    }

}
