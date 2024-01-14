package com.glodblock.github.ae2netanalyser.datagen;

import appeng.core.definitions.AEItems;
import appeng.datagen.providers.tags.ConventionTags;
import com.glodblock.github.ae2netanalyser.AEAnalyser;
import com.glodblock.github.ae2netanalyser.common.AEAItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class AEARecipeProvider extends RecipeProvider {

    static String C = "has_item";

    public AEARecipeProvider(PackOutput p) {
        super(p);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> c) {
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, AEAItems.ANALYSER)
                .pattern("C C")
                .pattern("ILI")
                .pattern("MIM")
                .define('C', ConventionTags.COPPER_INGOT)
                .define('I', ConventionTags.IRON_INGOT)
                .define('L', AEItems.LOGIC_PROCESSOR)
                .define('M', AEItems.SKY_DUST)
                .unlockedBy(C, has(AEAItems.ANALYSER))
                .save(c, AEAnalyser.id("analyser"));
    }

}
