package com.glodblock.github.extendedae.recipe;

import com.glodblock.github.extendedae.recipe.util.FluidIngredient;
import com.glodblock.github.extendedae.recipe.util.IngredientStack;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class CrystalAssemblerRecipeBuilder {

    protected List<IngredientStack.Item> inputs = new ArrayList<>();
    protected IngredientStack.Fluid fluid = null;
    public ItemStack output;

    public CrystalAssemblerRecipeBuilder(ItemStack output) {
        this.output = output.copy();
    }

    public static CrystalAssemblerRecipeBuilder assemble(ItemStack stack) {
        return new CrystalAssemblerRecipeBuilder(stack);
    }

    public static CrystalAssemblerRecipeBuilder assemble(ItemLike stack) {
        return new CrystalAssemblerRecipeBuilder(new ItemStack(stack));
    }

    public static CrystalAssemblerRecipeBuilder assemble(ItemLike stack, int count) {
        return new CrystalAssemblerRecipeBuilder(new ItemStack(stack, count));
    }

    public CrystalAssemblerRecipeBuilder fluid(FluidStack fluid) {
        this.fluid = IngredientStack.of(fluid);
        return this;
    }

    public CrystalAssemblerRecipeBuilder fluid(Fluid fluid, int amount) {
        this.fluid = IngredientStack.of(new FluidStack(fluid, amount));
        return this;
    }

    public CrystalAssemblerRecipeBuilder fluid(TagKey<Fluid> tag, int amount) {
        this.fluid = IngredientStack.of(new FluidIngredient(new FluidIngredient.TagValue(tag)), amount);
        return this;
    }

    public CrystalAssemblerRecipeBuilder input(ItemStack item) {
        this.inputs.add(IngredientStack.of(item));
        return this;
    }

    public CrystalAssemblerRecipeBuilder input(ItemLike item) {
        this.inputs.add(IngredientStack.of(new ItemStack(item)));
        return this;
    }

    public CrystalAssemblerRecipeBuilder input(ItemLike item, int count) {
        this.inputs.add(IngredientStack.of(new ItemStack(item, count)));
        return this;
    }

    public CrystalAssemblerRecipeBuilder input(TagKey<Item> tag) {
        this.inputs.add(IngredientStack.of(Ingredient.of(tag), 1));
        return this;
    }

    public CrystalAssemblerRecipeBuilder input(TagKey<Item> tag, int count) {
        this.inputs.add(IngredientStack.of(Ingredient.of(tag), count));
        return this;
    }

    public void save(RecipeOutput consumer, ResourceLocation id) {
        var recipe = new CrystalAssemblerRecipe(this.output, this.inputs, this.fluid);
        consumer.accept(id, recipe, null);
    }

}
