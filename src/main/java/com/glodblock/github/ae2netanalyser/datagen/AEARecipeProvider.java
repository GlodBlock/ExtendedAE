package com.glodblock.github.ae2netanalyser.datagen;

import appeng.core.ConventionTags;
import appeng.core.definitions.AEItems;
import com.glodblock.github.ae2netanalyser.AEAnalyser;
import com.glodblock.github.ae2netanalyser.common.AEASingletons;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class AEARecipeProvider extends RecipeProvider {

    static String C = "has_item";

    public AEARecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        this.shaped(RecipeCategory.MISC, AEASingletons.ANALYSER)
                .pattern("C C")
                .pattern("ILI")
                .pattern("MIM")
                .define('C', ConventionTags.COPPER_INGOT)
                .define('I', ConventionTags.IRON_INGOT)
                .define('L', AEItems.LOGIC_PROCESSOR)
                .define('M', AEItems.SKY_DUST)
                .unlockedBy(C, has(AEASingletons.ANALYSER))
                .save(this.output, AEAnalyser.stringId("analyser"));
    }

    public static class Runner extends RecipeProvider.Runner {

        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }

        @Override
        protected @NotNull RecipeProvider createRecipeProvider(HolderLookup.@NotNull Provider registries, @NotNull RecipeOutput output) {
            return new AEARecipeProvider(registries, output);
        }

        @Override
        public @NotNull String getName() {
            return "AE2 Network Analyzer Recipes";
        }

    }

}
