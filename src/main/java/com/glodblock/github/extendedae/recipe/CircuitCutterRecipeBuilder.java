package com.glodblock.github.extendedae.recipe;

import com.glodblock.github.extendedae.recipe.util.IngredientStack;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class CircuitCutterRecipeBuilder {

    protected IngredientStack.Item input;
    public ItemStack output;

    public CircuitCutterRecipeBuilder(ItemStack output) {
        this.output = output.copy();
    }

    public static CircuitCutterRecipeBuilder cut(ItemStack stack) {
        return new CircuitCutterRecipeBuilder(stack);
    }

    public static CircuitCutterRecipeBuilder cut(ItemLike stack) {
        return new CircuitCutterRecipeBuilder(new ItemStack(stack));
    }

    public static CircuitCutterRecipeBuilder cut(ItemLike stack, int count) {
        return new CircuitCutterRecipeBuilder(new ItemStack(stack, count));
    }

    public CircuitCutterRecipeBuilder input(ItemStack item) {
        this.input = IngredientStack.of(item);
        return this;
    }

    public CircuitCutterRecipeBuilder input(ItemLike item) {
        this.input = IngredientStack.of(new ItemStack(item));
        return this;
    }

    public CircuitCutterRecipeBuilder input(ItemLike item, int count) {
        this.input = IngredientStack.of(new ItemStack(item, count));
        return this;
    }

    public CircuitCutterRecipeBuilder input(TagKey<Item> tag) {
        this.input = IngredientStack.of(Ingredient.of(tag), 1);
        return this;
    }

    public CircuitCutterRecipeBuilder input(TagKey<Item> tag, int count) {
        this.input = IngredientStack.of(Ingredient.of(tag), count);
        return this;
    }

    public void save(RecipeOutput consumer, ResourceLocation id) {
        var recipe = new CircuitCutterRecipe(this.output, this.input);
        consumer.accept(id, recipe, null);
    }

}
