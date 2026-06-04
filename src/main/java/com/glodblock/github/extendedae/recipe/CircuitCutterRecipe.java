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

import java.util.Collections;
import java.util.List;

public class CircuitCutterRecipe extends MechanicsRecipe<RecipeInput> {

    public static final Identifier ID = ExtendedAE.id("circuit_cutter");
    public static final RecipeType<@NotNull CircuitCutterRecipe> TYPE = RecipeType.simple(ID);

    protected final IngredientStack.Item input;
    public final ItemStackTemplate output;
    public final long energy;

    public CircuitCutterRecipe(ItemStackTemplate output, IngredientStack.Item input, long energy) {
        this.output = output;
        this.input = input;
        this.energy = energy;
    }

    public IngredientStack.Item getInput() {
        return this.input;
    }

    public List<IngredientStack<?, ?>> getSample() {
        return Collections.singletonList(this.input.sample());
    }

    @Override
    public @NotNull RecipeSerializer<? extends @NotNull Recipe<@NotNull RecipeInput>> getSerializer() {
        return CircuitCutterRecipeSerializer.INSTANCE;
    }

    @Override
    public @NotNull RecipeType<? extends @NotNull Recipe<@NotNull RecipeInput>> getType() {
        return TYPE;
    }

}
