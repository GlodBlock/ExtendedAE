package com.glodblock.github.extendedae.recipe;

import appeng.recipes.MechanicsRecipe;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.glodium.recipe.stack.IngredientStack;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class CrystalFixerRecipe extends MechanicsRecipe<RecipeInput> {

    public static final Identifier ID = ExtendedAE.id("crystal_fixer");
    public static final RecipeType<@NotNull CrystalFixerRecipe> TYPE = RecipeType.simple(ID);
    public static final int FULL_CHANCE = 10000;
    protected final Block input;
    protected final Block output;
    protected final int chance;
    protected final IngredientStack.Item fuel;

    // 10000 means 100% chance
    public CrystalFixerRecipe(ItemStackTemplate input, ItemStackTemplate output, IngredientStack.Item fuel, int chance) {
        this(asBlock(input), asBlock(output), fuel, chance);
    }

    public CrystalFixerRecipe(Block input, Block output, IngredientStack.Item fuel, int chance) {
        this.input = input;
        this.output = output;
        this.fuel = fuel;
        this.chance = chance;
    }

    private static Block asBlock(ItemStackTemplate stack) {
        return ((BlockItem) stack.item().value()).getBlock();
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
    public @NotNull RecipeSerializer<? extends @NotNull Recipe<@NotNull RecipeInput>> getSerializer() {
        return CrystalFixerRecipeSerializer.INSTANCE;
    }

    @Override
    public @NotNull RecipeType<? extends @NotNull Recipe<@NotNull RecipeInput>> getType() {
        return TYPE;
    }

}
