package com.glodblock.github.extendedae.recipe;

import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.glodium.recipe.stack.IngredientStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;

public class CrystalFixerRecipe implements Recipe<RecipeInput> {

    public static final ResourceLocation ID = ExtendedAE.id("crystal_fixer");
    public static final RecipeType<CrystalFixerRecipe> TYPE = RecipeType.simple(ID);
    public static final int FULL_CHANCE = 10000;
    private static final IdentityHashMap<Block, RecipeHolder<CrystalFixerRecipe>> FAST_LOOKUP = new IdentityHashMap<>();
    protected final Block input;
    protected final Block output;
    protected final int chance;
    protected final IngredientStack.Item fuel;

    // 10000 means 100% chance
    public CrystalFixerRecipe(ItemStack input, ItemStack output, IngredientStack.Item fuel, int chance) {
        this(asBlock(input), asBlock(output), fuel, chance);
    }

    public CrystalFixerRecipe(Block input, Block output, IngredientStack.Item fuel, int chance) {
        this.input = input;
        this.output = output;
        this.fuel = fuel;
        this.chance = chance;
    }

    public static void clearLookup() {
        FAST_LOOKUP.clear();
    }

    private static Block asBlock(ItemStack stack) {
        return ((BlockItem) stack.getItem()).getBlock();
    }

    @Nullable
    public static RecipeHolder<CrystalFixerRecipe> lookup(Block block, @NotNull Level world) {
        if (FAST_LOOKUP.isEmpty()) {
            initLookup(world);
        }
        return FAST_LOOKUP.get(block);
    }

    public boolean roll(RandomSource random) {
        if (this.chance == FULL_CHANCE) {
            return true;
        }
        return random.nextInt(FULL_CHANCE) < this.chance;
    }

    public double getChance() {
        return (double) this.chance / FULL_CHANCE;
    }

    public Block getOutput() {
        return this.output;
    }

    public Block getInput() {
        return this.input;
    }

    public IngredientStack.Item getFuel() {
        return this.fuel.sample();
    }

    @Override
    public boolean matches(@NotNull RecipeInput pContainer, @NotNull Level pLevel) {
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull RecipeInput pContainer, @NotNull HolderLookup.Provider pRegistryAccess) {
        return getResultItem(pRegistryAccess).copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull HolderLookup.Provider pRegistryAccess) {
        return new ItemStack(this.output);
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return CrystalFixerRecipeSerializer.INSTANCE;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return TYPE;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    private static void initLookup(Level world) {
        var recipes = world.getRecipeManager().byType(TYPE);
        for (var recipe : recipes) {
            FAST_LOOKUP.put(recipe.value().getInput(), recipe);
        }
    }

}
