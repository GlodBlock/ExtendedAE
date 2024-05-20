package com.glodblock.github.extendedae.xmod.emi.recipes;

import com.glodblock.github.extendedae.recipe.util.FluidIngredient;
import com.glodblock.github.extendedae.recipe.util.IngredientStack;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class EMIStackUtil {

    public static EmiIngredient of(IngredientStack.Item stack) {
        return !stack.isEmpty() ? EmiIngredient.of((Ingredient) stack.getIngredient(), stack.getAmount()) : EmiStack.EMPTY;
    }

    public static EmiIngredient of(IngredientStack.Fluid stack) {
        FluidIngredient ingredient = (FluidIngredient) stack.getIngredient();
        List<EmiIngredient> list = new ArrayList<>();
        for (var fluid : ingredient.getFluid()) {
            list.add(EmiStack.of(fluid, stack.getAmount()));
        }
        return EmiIngredient.of(list, stack.getAmount());
    }

}
