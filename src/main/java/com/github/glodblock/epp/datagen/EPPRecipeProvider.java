package com.github.glodblock.epp.datagen;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.datagen.providers.tags.ConventionTags;
import com.github.glodblock.epp.EPP;
import com.github.glodblock.epp.common.EPPItemAndBlock;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class EPPRecipeProvider extends RecipeProvider {

    static String C = "has_item";

    public EPPRecipeProvider(PackOutput p) {
        super(p);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> c) {
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, EPPItemAndBlock.EX_PATTERN_PROVIDER)
                .pattern("PC")
                .pattern("CZ")
                .define('P', ConventionTags.PATTERN_PROVIDER)
                .define('C', AEItems.CAPACITY_CARD)
                .define('Z', AEItems.ENGINEERING_PROCESSOR)
                .unlockedBy(C, has(EPPItemAndBlock.EX_PATTERN_PROVIDER))
                .save(c, EPP.id("epp"));
        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.MISC, EPPItemAndBlock.EX_PATTERN_PROVIDER_PART)
                .requires(EPPItemAndBlock.EX_PATTERN_PROVIDER)
                .unlockedBy(C, has(EPPItemAndBlock.EX_PATTERN_PROVIDER_PART))
                .save(c, EPP.id("epp_part"));
        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.MISC, EPPItemAndBlock.EX_PATTERN_PROVIDER)
                .requires(EPPItemAndBlock.EX_PATTERN_PROVIDER_PART)
                .unlockedBy(C, has(EPPItemAndBlock.EX_PATTERN_PROVIDER))
                .save(c, EPP.id("epp_alt"));
        ShapelessRecipeBuilder
                .shapeless(RecipeCategory.MISC, EPPItemAndBlock.PATTERN_PROVIDER_UPGRADE)
                .requires(AEItems.CAPACITY_CARD, 2)
                .requires(AEItems.ENGINEERING_PROCESSOR)
                .unlockedBy(C, has(EPPItemAndBlock.PATTERN_PROVIDER_UPGRADE))
                .save(c, EPP.id("epp_upgrade"));
    }
}
