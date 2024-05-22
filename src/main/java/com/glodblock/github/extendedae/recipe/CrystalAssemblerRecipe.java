package com.glodblock.github.extendedae.recipe;

import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.recipe.util.IngredientStack;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CrystalAssemblerRecipe implements Recipe<Container> {

    public static final ResourceLocation ID = ExtendedAE.id("crystal_assembler");
    public static final RecipeType<CrystalAssemblerRecipe> TYPE = RecipeType.simple(ID);

    protected final List<IngredientStack.Item> inputs;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    protected final Optional<IngredientStack.Fluid> fluid;
    public final ItemStack output;

    public CrystalAssemblerRecipe(ItemStack output, List<IngredientStack.Item> inputs, IngredientStack.Fluid fluid) {
        this(output, inputs, Optional.ofNullable(fluid));
    }

    public CrystalAssemblerRecipe(ItemStack output, List<IngredientStack.Item> inputs, Optional<IngredientStack.Fluid> fluid) {
        this.output = output;
        this.inputs = inputs;
        this.fluid = fluid;
    }

    public List<IngredientStack.Item> getInputs() {
        return this.inputs;
    }

    @Nullable
    public IngredientStack.Fluid getFluid() {
        return this.fluid.orElse(null);
    }

    public List<IngredientStack<?>> getSample() {
        List<IngredientStack<?>> sample = new ArrayList<>();
        for (var in : this.inputs) {
            if (!in.isEmpty()) {
                sample.add(in.sample());
            }
        }
        this.fluid.ifPresent(ingredientStack -> sample.add(ingredientStack.sample()));
        return sample;
    }

    @Override
    public boolean matches(@NotNull Container pContainer, @NotNull Level pLevel) {
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull Container pContainer, @NotNull RegistryAccess pRegistryAccess) {
        return getResultItem(pRegistryAccess).copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull RegistryAccess pRegistryAccess) {
        return this.output;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return CrystalAssemblerRecipeSerializer.INSTANCE;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return TYPE;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

}
