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
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CrystalAssemblerRecipeBuilder {

    private final HolderGetter<@NotNull Item> itemGetter;
    private final HolderGetter<@NotNull Fluid> fluidGetter;
    protected List<IngredientStack.Item> inputs = new ArrayList<>();
    protected IngredientStack.Fluid fluid = null;
    public ItemStackTemplate output;
    public long energy = 2000;

    public CrystalAssemblerRecipeBuilder(ItemStackTemplate output, HolderGetter<@NotNull Item> itemGetter, HolderGetter<@NotNull Fluid> fluidGetter) {
        this.output = output;
        this.itemGetter = itemGetter;
        this.fluidGetter = fluidGetter;
    }

    public static CrystalAssemblerRecipeBuilder assemble(ItemStackTemplate stack, HolderGetter<@NotNull Item> itemGetter, HolderGetter<@NotNull Fluid> fluidGetter) {
        return new CrystalAssemblerRecipeBuilder(stack, itemGetter, fluidGetter);
    }

    public static CrystalAssemblerRecipeBuilder assemble(ItemLike stack, HolderGetter<@NotNull Item> itemGetter, HolderGetter<@NotNull Fluid> fluidGetter) {
        return new CrystalAssemblerRecipeBuilder(new ItemStackTemplate(stack.asItem()), itemGetter, fluidGetter);
    }

    public static CrystalAssemblerRecipeBuilder assemble(ItemLike stack, int count, HolderGetter<@NotNull Item> itemGetter, HolderGetter<@NotNull Fluid> fluidGetter) {
        return new CrystalAssemblerRecipeBuilder(new ItemStackTemplate(stack.asItem(), count), itemGetter, fluidGetter);
    }

    public CrystalAssemblerRecipeBuilder fluid(FluidStackTemplate fluid) {
        this.fluid = IngredientStack.of(fluid);
        return this;
    }

    public CrystalAssemblerRecipeBuilder fluid(Fluid fluid, int amount) {
        this.fluid = IngredientStack.of(fluid, amount);
        return this;
    }

    public CrystalAssemblerRecipeBuilder fluid(TagKey<@NotNull Fluid> tag, int amount) {
        this.fluid = IngredientStack.of(FluidIngredient.of(this.fluidGetter.getOrThrow(tag)), amount);
        return this;
    }

    public CrystalAssemblerRecipeBuilder input(ItemStackTemplate item) {
        this.inputs.add(IngredientStack.of(item));
        return this;
    }

    public CrystalAssemblerRecipeBuilder input(ItemLike item) {
        this.inputs.add(IngredientStack.of(item, 1));
        return this;
    }

    public CrystalAssemblerRecipeBuilder input(ItemLike item, int count) {
        this.inputs.add(IngredientStack.of(item, count));
        return this;
    }

    public CrystalAssemblerRecipeBuilder input(TagKey<@NotNull Item> tag) {
        this.inputs.add(IngredientStack.of(Ingredient.of(this.itemGetter.getOrThrow(tag)), 1));
        return this;
    }

    public CrystalAssemblerRecipeBuilder input(Ingredient ingredient) {
        this.inputs.add(IngredientStack.of(ingredient, 1));
        return this;
    }

    public CrystalAssemblerRecipeBuilder input(TagKey<@NotNull Item> tag, int count) {
        this.inputs.add(IngredientStack.of(Ingredient.of(this.itemGetter.getOrThrow(tag)), count));
        return this;
    }

    public CrystalAssemblerRecipeBuilder power(long energy) {
        this.energy = energy;
        return this;
    }

    public void save(RecipeOutput consumer, Identifier id) {
        var recipe = new CrystalAssemblerRecipe(this.output, this.inputs, this.fluid, this.energy);
        consumer.accept(ResourceKey.create(Registries.RECIPE, id), recipe, null);
    }

}
