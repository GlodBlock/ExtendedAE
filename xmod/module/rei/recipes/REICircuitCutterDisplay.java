package com.glodblock.github.extendedae.xmod.rei.recipes;

import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.recipe.CircuitCutterRecipe;
import com.google.common.collect.ImmutableList;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;
import java.util.Optional;

public class REICircuitCutterDisplay implements Display {

    public static final CategoryIdentifier<REICircuitCutterDisplay> ID = CategoryIdentifier.of(ExtendedAE.id("rei_circuit_cutter"));
    private final RecipeHolder<CircuitCutterRecipe> holder;
    private final List<EntryIngredient> inputs;
    private final List<EntryIngredient> outputs;

    public REICircuitCutterDisplay(RecipeHolder<CircuitCutterRecipe> holder) {
        this.holder = holder;
        var recipe = holder.value();
        this.inputs = ImmutableList.of(REIStackUtil.of(recipe.getInput()));
        this.outputs = ImmutableList.of(EntryIngredients.of(recipe.output));
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        return this.inputs;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return this.outputs;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return ID;
    }

    @Override
    public Optional<ResourceLocation> getDisplayLocation() {
        return Optional.of(holder.id());
    }

}
