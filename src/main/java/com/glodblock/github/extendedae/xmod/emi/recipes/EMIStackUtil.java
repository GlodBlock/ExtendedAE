package com.glodblock.github.extendedae.xmod.emi.recipes;

import com.glodblock.github.glodium.recipe.stack.IngredientStack;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

import java.util.ArrayList;
import java.util.List;

public class EMIStackUtil {

    public static EmiIngredient of(IngredientStack.Item stack) {
        return !stack.isEmpty() ? EmiIngredient.of((Ingredient) stack.getIngredient(), stack.getAmount()) : EmiStack.EMPTY;
    }

    public static EmiIngredient of(IngredientStack.Fluid stack) {
        FluidIngredient ingredient = stack.getIngredient();
        List<EmiIngredient> list = new ArrayList<>();
        for (var fluid : ingredient.getStacks()) {
            list.add(EmiStack.of(fluid.getFluid(), fluid.getAmount()));
        }
        return EmiIngredient.of(list, stack.getAmount());
    }

}
