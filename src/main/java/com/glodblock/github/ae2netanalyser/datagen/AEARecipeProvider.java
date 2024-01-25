package com.glodblock.github.ae2netanalyser.datagen;

import appeng.core.definitions.AEItems;
import appeng.datagen.providers.tags.ConventionTags;
import com.glodblock.github.ae2netanalyser.AEAnalyzer;
import com.glodblock.github.ae2netanalyser.common.AEAItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class AEARecipeProvider extends RecipeProvider {

    static String C = "has_item";

    public AEARecipeProvider(FabricDataOutput p) {
        super(p);
    }

    @Override
    public void buildRecipes(@NotNull Consumer<FinishedRecipe> c) {
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
                .save(c, AEAnalyzer.id("analyser"));
    }

}
