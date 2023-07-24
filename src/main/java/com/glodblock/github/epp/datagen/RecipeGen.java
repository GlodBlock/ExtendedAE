package com.glodblock.github.epp.datagen;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import com.glodblock.github.epp.EPP;
import com.glodblock.github.epp.common.EPPItemAndBlock;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;

import java.util.function.Consumer;

public class RecipeGen extends FabricRecipeProvider {

    public RecipeGen(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    protected void generateRecipes(Consumer<RecipeJsonProvider> exporter) {
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
        ShapedRecipeJsonBuilder
                .create(EPPItemAndBlock.EX_PATTERN_PROVIDER)
                .pattern("PC")
                .pattern("CZ")
                .input('P', AEBlocks.PATTERN_PROVIDER)
                .input('C', AEItems.CAPACITY_CARD)
                .input('Z', AEItems.ENGINEERING_PROCESSOR)
                .criterion(FabricRecipeProvider.hasItem(EPPItemAndBlock.EX_PATTERN_PROVIDER), FabricRecipeProvider.conditionsFromItem(EPPItemAndBlock.EX_PATTERN_PROVIDER))
                .offerTo(exporter, EPP.id("epp"));
    }
}
