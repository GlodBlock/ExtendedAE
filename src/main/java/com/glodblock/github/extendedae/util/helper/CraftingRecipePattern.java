package com.glodblock.github.extendedae.util.helper;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

public interface CraftingRecipePattern {

    ItemStack getOutput();

    RecipeHolder<?> getRecipeHolder();

}
