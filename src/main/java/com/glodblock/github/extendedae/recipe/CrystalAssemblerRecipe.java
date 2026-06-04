package com.glodblock.github.extendedae.recipe;

import appeng.recipes.MechanicsRecipe;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.glodium.recipe.stack.IngredientStack;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CrystalAssemblerRecipe extends MechanicsRecipe<RecipeInput> {

    public static final Identifier ID = ExtendedAE.id("crystal_assembler");
    public static final RecipeType<@NotNull CrystalAssemblerRecipe> TYPE = RecipeType.simple(ID);

    protected final List<IngredientStack.Item> inputs;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    protected final Optional<IngredientStack.Fluid> fluid;
    public final ItemStackTemplate output;
    public final long energy;

    public CrystalAssemblerRecipe(ItemStackTemplate output, List<IngredientStack.Item> inputs, IngredientStack.Fluid fluid, long energy) {
        this.output = output;
        this.inputs = inputs;
        this.fluid = Optional.ofNullable(fluid);
        this.energy = energy;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public CrystalAssemblerRecipe(ItemStackTemplate output, List<IngredientStack.Item> inputs, Optional<IngredientStack.Fluid> fluid, long energy) {
        this.output = output;
        this.inputs = inputs;
        this.fluid = fluid;
        this.energy = energy;
    }

    public List<IngredientStack.Item> getInputs() {
        return this.inputs;
    }

    @Nullable
    public IngredientStack.Fluid getFluid() {
        return this.fluid.orElse(null);
    }

    public List<IngredientStack<?, ?>> getSample() {
        List<IngredientStack<?, ?>> sample = new ArrayList<>();
        for (var in : this.inputs) {
            if (!in.isEmpty()) {
                sample.add(in.sample());
            }
        }
        this.fluid.ifPresent(ingredientStack -> sample.add(ingredientStack.sample()));
        return sample;
    }

    @Override
    public @NotNull RecipeSerializer<? extends @NotNull Recipe<@NotNull RecipeInput>> getSerializer() {
        return CrystalAssemblerRecipeSerializer.INSTANCE;
    }

    @Override
    public @NotNull RecipeType<? extends @NotNull Recipe<@NotNull RecipeInput>> getType() {
        return TYPE;
    }

}
