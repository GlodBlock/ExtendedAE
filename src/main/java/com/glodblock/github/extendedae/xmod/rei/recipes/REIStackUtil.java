package com.glodblock.github.extendedae.xmod.rei.recipes;

import com.glodblock.github.glodium.recipe.stack.IngredientStack;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;

public class REIStackUtil {

    public static EntryIngredient of(IngredientStack.Item stack) {
        if (!stack.isEmpty()) {
            var stacks = stack.getIngredient().getItems();
            var result = EntryIngredient.builder(stacks.length);
            for (var ing : stacks) {
                if (!ing.isEmpty()) {
                    result.add(EntryStacks.of(ing.copyWithCount(stack.getAmount())));
                }
            }
            return result.build();
        }
        return EntryIngredient.empty();
    }

    public static EntryIngredient of(IngredientStack.Fluid stack) {
        if (!stack.isEmpty()) {
            var stacks = stack.getIngredient().getStacks();
            var result = EntryIngredient.builder(stacks.length);
            for (var ing : stacks) {
                if (!ing.isEmpty()) {
                    result.add(EntryStacks.of(ing.getFluid(), stack.getAmount()));
                }
            }
            return result.build();
        }
        return EntryIngredient.empty();
    }

}
