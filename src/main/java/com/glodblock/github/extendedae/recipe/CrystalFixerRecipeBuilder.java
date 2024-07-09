package com.glodblock.github.extendedae.recipe;

import com.glodblock.github.glodium.recipe.stack.IngredientStack;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public class CrystalFixerRecipeBuilder {

    protected Block input;
    protected Block output;
    protected IngredientStack.Item fuel;
    protected int chance;

    public CrystalFixerRecipeBuilder(Block input, Block output) {
        this.input = input;
        this.output = output;
    }

    public static CrystalFixerRecipeBuilder fixer(Block input, Block output) {
        return new CrystalFixerRecipeBuilder(input, output);
    }

    public CrystalFixerRecipeBuilder fuel(ItemStack item) {
        this.fuel = IngredientStack.of(item.copyWithCount(1));
        return this;
    }

    public CrystalFixerRecipeBuilder fuel(ItemLike item) {
        this.fuel = IngredientStack.of(new ItemStack(item));
        return this;
    }

    public CrystalFixerRecipeBuilder fuel(TagKey<Item> tag) {
        this.fuel = IngredientStack.of(Ingredient.of(tag), 1);
        return this;
    }

    public CrystalFixerRecipeBuilder chance(int chance) {
        this.chance = chance;
        return this;
    }

    public CrystalFixerRecipeBuilder chance(double chance) {
        this.chance = (int) (chance * CrystalFixerRecipe.FULL_CHANCE);
        return this;
    }

    public void save(RecipeOutput consumer, ResourceLocation id) {
        var recipe = new CrystalFixerRecipe(this.input, this.output, this.fuel, this.chance);
        consumer.accept(id, recipe, null);
    }

}
