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

public class CircuitCutterRecipeBuilder {

    private final HolderGetter<@NotNull Item> getter;
    protected IngredientStack.Item input;
    public ItemStackTemplate output;

    public CircuitCutterRecipeBuilder(ItemStackTemplate output, HolderGetter<@NotNull Item> getter) {
        this.output = output;
        this.getter = getter;
    }

    public static CircuitCutterRecipeBuilder cut(ItemStackTemplate stack, HolderGetter<@NotNull Item> getter) {
        return new CircuitCutterRecipeBuilder(stack, getter);
    }

    public static CircuitCutterRecipeBuilder cut(ItemLike stack, HolderGetter<@NotNull Item> getter) {
        return new CircuitCutterRecipeBuilder(new ItemStackTemplate(stack.asItem()), getter);
    }

    public static CircuitCutterRecipeBuilder cut(ItemLike stack, int count, HolderGetter<@NotNull Item> getter) {
        return new CircuitCutterRecipeBuilder(new ItemStackTemplate(stack.asItem(), count), getter);
    }

    public CircuitCutterRecipeBuilder input(ItemStackTemplate item) {
        this.input = IngredientStack.of(item);
        return this;
    }

    public CircuitCutterRecipeBuilder input(ItemLike item) {
        this.input = IngredientStack.of(item, 1);
        return this;
    }

    public CircuitCutterRecipeBuilder input(ItemLike item, int count) {
        this.input = IngredientStack.of(item, count);
        return this;
    }

    public CircuitCutterRecipeBuilder input(TagKey<@NotNull Item> tag) {
        this.input = IngredientStack.of(Ingredient.of(this.getter.getOrThrow(tag)), 1);
        return this;
    }

    public CircuitCutterRecipeBuilder input(TagKey<@NotNull Item> tag, int count) {
        this.input = IngredientStack.of(Ingredient.of(this.getter.getOrThrow(tag)), count);
        return this;
    }

    public void save(RecipeOutput consumer, Identifier id) {
        var recipe = new CircuitCutterRecipe(this.output, this.input);
        consumer.accept(ResourceKey.create(Registries.RECIPE, id), recipe, null);
    }

}
