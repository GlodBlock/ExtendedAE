package com.glodblock.github.extendedae.xmod.emi;

import com.glodblock.github.extendedae.common.EAEItemAndBlock;
import com.glodblock.github.extendedae.recipe.CrystalAssemblerRecipe;
import com.glodblock.github.extendedae.util.Ae2ReflectClient;
import com.glodblock.github.extendedae.xmod.emi.recipes.EMICrystalAssemblerRecipe;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiInfoRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@EmiEntrypoint
public class EMIPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        registry.addGenericStackProvider(new PatternSlotProvider());
        registry.addWorkstation(Ae2ReflectClient.getInscribeRecipe(), EmiStack.of(EAEItemAndBlock.EX_INSCRIBER));
        registry.addWorkstation(Ae2ReflectClient.getChargerRecipe(), EmiStack.of(EAEItemAndBlock.EX_CHARGER));
        registry.setDefaultComparison(EAEItemAndBlock.INFINITY_CELL,
                Comparison.compareData(s -> EAEItemAndBlock.INFINITY_CELL.getRecord(s.getItemStack()))
        );
        registry.addCategory(EMICrystalAssemblerRecipe.CATEGORY);
        registry.addWorkstation(EMICrystalAssemblerRecipe.CATEGORY, EmiStack.of(EAEItemAndBlock.CRYSTAL_ASSEMBLER));
        adaptRecipeType(registry, CrystalAssemblerRecipe.TYPE, EMICrystalAssemblerRecipe::new);

        addInfo(registry, EAEItemAndBlock.ENTRO_CRYSTAL, Component.translatable("emi.extendedae.desc.entro_crystal"));
        addInfo(registry, EAEItemAndBlock.ENTRO_SEED, Component.translatable("emi.extendedae.desc.entro_seed"));
    }

    private static <C extends Container, T extends Recipe<C>> void adaptRecipeType(EmiRegistry registry, RecipeType<T> recipeType, Function<RecipeHolder<T>, ? extends EmiRecipe> adapter) {
        registry.getRecipeManager().getAllRecipesFor(recipeType)
                .stream()
                .map(adapter)
                .forEach(registry::addRecipe);
    }

    private static void addInfo(EmiRegistry registry, ItemLike item, Component... desc) {
        registry.addRecipe(
                new EmiInfoRecipe(
                        List.of(EmiStack.of(item)),
                        Arrays.stream(desc).toList(),
                        null
                )
        );
    }

}
