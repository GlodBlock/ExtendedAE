package com.glodblock.github.extendedae.recipe;

import com.glodblock.github.glodium.recipe.stack.IngredientStack;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class CrystalFixerRecipeBuilder {

    private final HolderGetter<@NotNull Item> getter;
    protected Block input;
    protected Block output;
    protected IngredientStack.Item fuel;
    protected int chance;

    public CrystalFixerRecipeBuilder(Block input, Block output, HolderGetter<@NotNull Item> getter) {
        this.input = input;
        this.output = output;
        this.getter = getter;
    }

    public static CrystalFixerRecipeBuilder fixer(Block input, Block output, HolderGetter<@NotNull Item> getter) {
        return new CrystalFixerRecipeBuilder(input, output, getter);
    }

    public CrystalFixerRecipeBuilder fuel(ItemStack item) {
        this.fuel = IngredientStack.of(item.copyWithCount(1));
        return this;
    }

    public CrystalFixerRecipeBuilder fuel(ItemLike item) {
        this.fuel = IngredientStack.of(new ItemStack(item));
        return this;
    }

    public CrystalFixerRecipeBuilder fuel(TagKey<@NotNull Item> tag) {
        this.fuel = IngredientStack.of(Ingredient.of(this.getter.getOrThrow(tag)), 1);
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

    public void save(RecipeOutput consumer, Identifier id) {
        var recipe = new CrystalFixerRecipe(this.input, this.output, this.fuel, this.chance);
        consumer.accept(ResourceKey.create(Registries.RECIPE, id), recipe, null);
    }

}
