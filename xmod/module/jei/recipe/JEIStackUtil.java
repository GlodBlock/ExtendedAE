package com.glodblock.github.extendedae.xmod.jei.recipe;

import com.glodblock.github.glodium.recipe.stack.IngredientStack;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;

public class JEIStackUtil {

    public static void addItem(IngredientStack.Item stack, IRecipeSlotBuilder slot) {
        for (var item : stack.getIngredient().getItems()) {
            slot.addItemStack(item.copyWithCount(stack.getAmount()));
        }
    }

    public static void addFluid(IngredientStack.Fluid stack, IRecipeSlotBuilder slot) {
        slot.setFluidRenderer(stack.getAmount(), false, 16, 16);
        for (var fluid : stack.getIngredient().getStacks()) {
            slot.addFluidStack(fluid.getFluid(), stack.getAmount());
        }
    }

}
