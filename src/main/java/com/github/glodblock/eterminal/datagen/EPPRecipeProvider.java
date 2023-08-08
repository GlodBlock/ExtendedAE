package com.github.glodblock.eterminal.datagen;

import appeng.core.definitions.AEItems;
import appeng.core.definitions.AEParts;
import com.github.glodblock.eterminal.EnhancedTerminal;
import com.github.glodblock.eterminal.common.ETerminalItemAndBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class EPPRecipeProvider extends RecipeProvider {

    static String C = "has_item";

    public EPPRecipeProvider(DataGenerator p) {
        super(p);
    }

    @Override
    protected void buildCraftingRecipes(@NotNull Consumer<FinishedRecipe> c) {
        // Extended Pattern Access Terminal
        ShapedRecipeBuilder
                .shaped(ETerminalItemAndBlock.EX_PATTERN_TERMINAL)
                .pattern(" L ")
                .pattern("CPC")
                .pattern(" C ")
                .define('L', Blocks.REDSTONE_LAMP)
                .define('P', AEParts.PATTERN_ACCESS_TERMINAL)
                .define('C', AEItems.LOGIC_PROCESSOR)
                .unlockedBy(C, has(ETerminalItemAndBlock.EX_PATTERN_TERMINAL))
                .save(c, EnhancedTerminal.id("epa"));
    }
}
