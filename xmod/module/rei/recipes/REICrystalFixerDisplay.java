package com.glodblock.github.extendedae.xmod.rei.recipes;

import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.recipe.CrystalFixerRecipe;
import com.google.common.collect.ImmutableList;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;
import java.util.Optional;

public class REICrystalFixerDisplay implements Display {

    public static final CategoryIdentifier<REICrystalFixerDisplay> ID = CategoryIdentifier.of(ExtendedAE.id("rei_circuit_cutter"));
    private final RecipeHolder<CrystalFixerRecipe> holder;
    private final List<EntryIngredient> inputs;
    private final List<EntryIngredient> outputs;

    public REICrystalFixerDisplay(RecipeHolder<CrystalFixerRecipe> holder) {
        this.holder = holder;
        var recipe = holder.value();
        this.inputs = ImmutableList.of(EntryIngredients.of(recipe.getInput()), REIStackUtil.of(recipe.getFuel()));
        this.outputs = ImmutableList.of(EntryIngredients.of(recipe.getOutput()));
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

    public EntryIngredient getInput() {
        return this.inputs.getFirst();
    }

    public EntryIngredient getFuel() {
        return this.inputs.getLast();
    }

    public double getChance() {
        return this.holder.value().getChance();
    }

    @Override
    public Optional<ResourceLocation> getDisplayLocation() {
        return Optional.of(holder.id());
    }

}
