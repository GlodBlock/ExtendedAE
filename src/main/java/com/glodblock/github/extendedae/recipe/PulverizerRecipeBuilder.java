package com.glodblock.github.extendedae.recipe;

import com.glodblock.github.glodium.recipe.stack.IngredientStack;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PulverizerRecipeBuilder {

    protected final IngredientStack.Item input;
    protected final List<PulverizerRecipe.ChanceOutput> outputs = new ArrayList<>();
    protected long energy = 1000;

    public PulverizerRecipeBuilder(IngredientStack.Item input) {
        this.input = input;
    }

    public static PulverizerRecipeBuilder crush(ItemStackTemplate stack) {
        return new PulverizerRecipeBuilder(IngredientStack.of(stack));
    }

    public static PulverizerRecipeBuilder crush(ItemLike stack) {
        return new PulverizerRecipeBuilder(IngredientStack.of(new ItemStackTemplate(stack.asItem(), 1)));
    }

    public static PulverizerRecipeBuilder crush(ItemLike stack, int count) {
        return new PulverizerRecipeBuilder(IngredientStack.of(new ItemStackTemplate(stack.asItem(), count)));
    }

    public static PulverizerRecipeBuilder crush(TagKey<@NotNull Item> tag, HolderGetter<@NotNull Item> getter) {
        return new PulverizerRecipeBuilder(IngredientStack.of(Ingredient.of(getter.getOrThrow(tag)), 1));
    }

    public static PulverizerRecipeBuilder crush(TagKey<@NotNull Item> tag, int count, HolderGetter<@NotNull Item> getter) {
        return new PulverizerRecipeBuilder(IngredientStack.of(Ingredient.of(getter.getOrThrow(tag)), count));
    }

    public static PulverizerRecipeBuilder crush(Ingredient stack) {
        return new PulverizerRecipeBuilder(IngredientStack.of(stack, 1));
    }

    public static PulverizerRecipeBuilder crush(Ingredient stack, int count) {
        return new PulverizerRecipeBuilder(IngredientStack.of(stack, count));
    }

    public PulverizerRecipeBuilder output(ItemStackTemplate item) {
        this.outputs.add(PulverizerRecipe.ChanceOutput.of(item, 1));
        return this;
    }

    public PulverizerRecipeBuilder output(ItemStackTemplate item, double chance) {
        this.outputs.add(PulverizerRecipe.ChanceOutput.of(item, chance));
        return this;
    }

    public PulverizerRecipeBuilder output(ItemLike item) {
        this.outputs.add(PulverizerRecipe.ChanceOutput.of(new ItemStackTemplate(item.asItem(), 1), 1));
        return this;
    }

    public PulverizerRecipeBuilder output(ItemLike item, int count) {
        this.outputs.add(PulverizerRecipe.ChanceOutput.of(new ItemStackTemplate(item.asItem(), count), 1));
        return this;
    }

    public PulverizerRecipeBuilder output(ItemLike item, int count, double chance) {
        this.outputs.add(PulverizerRecipe.ChanceOutput.of(new ItemStackTemplate(item.asItem(), count), chance));
        return this;
    }

    public PulverizerRecipeBuilder power(long energy) {
        this.energy = energy;
        return this;
    }

    public void save(RecipeOutput consumer, Identifier id) {
        var recipe = new PulverizerRecipe(this.outputs, this.input, this.energy);
        consumer.accept(ResourceKey.create(Registries.RECIPE, id), recipe, null);
    }

}
