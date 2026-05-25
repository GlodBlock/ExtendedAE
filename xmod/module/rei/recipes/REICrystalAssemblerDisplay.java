package com.glodblock.github.extendedae.xmod.rei.recipes;

import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.recipe.CrystalAssemblerRecipe;
import com.google.common.collect.ImmutableList;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class REICrystalAssemblerDisplay implements Display {

    public static final CategoryIdentifier<REICrystalAssemblerDisplay> ID = CategoryIdentifier.of(ExtendedAE.id("rei_crystal_assembler"));
    private final RecipeHolder<CrystalAssemblerRecipe> holder;
    private final List<EntryIngredient> inputs;
    private final List<EntryIngredient> outputs;
    private final List<EntryIngredient> combined;
    private final EntryIngredient fluid;

    public REICrystalAssemblerDisplay(RecipeHolder<CrystalAssemblerRecipe> holder) {
        this.holder = holder;
        var recipe = holder.value();
        this.inputs = recipe.getInputs().stream().map(REIStackUtil::of).filter(o -> !o.isEmpty()).toList();
        this.fluid = recipe.getFluid() != null ? REIStackUtil.of(recipe.getFluid()) : EntryIngredient.empty();
        this.outputs = ImmutableList.of(EntryIngredients.of(recipe.output));
        this.combined = new ArrayList<>(this.inputs);
        if (!this.fluid.isEmpty()) {
            this.combined.addLast(this.fluid);
        }
    }

    public List<EntryIngredient> getInputItems() {
        return this.inputs;
    }

    public EntryIngredient getInputFluid() {
        return this.fluid;
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        return this.combined;
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
