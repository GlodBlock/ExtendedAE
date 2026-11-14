package com.glodblock.github.extendedae.recipe;

import appeng.recipes.MechanicsRecipe;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.glodium.recipe.stack.IngredientStack;
import com.glodblock.github.glodium.util.GlodUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class PulverizerRecipe extends MechanicsRecipe<RecipeInput> {

    public static final Identifier ID = ExtendedAE.id("pulverizer");
    public static final RecipeType<@NotNull PulverizerRecipe> TYPE = RecipeType.simple(ID);

    protected final IngredientStack.Item input;
    protected final List<ChanceOutput> outputs;
    public final long energy;

    public PulverizerRecipe(List<ChanceOutput> outputs, IngredientStack.Item input, long energy) {
        this.outputs = List.copyOf(outputs);
        this.input = input;
        this.energy = energy;
    }

    public IngredientStack.Item getInput() {
        return this.input;
    }

    public List<IngredientStack<?, ?>> getSample() {
        return Collections.singletonList(this.input.sample());
    }

    public List<ItemStack> getFullOutputs() {
        return this.outputs.stream().map(output -> output.stack.create()).toList();
    }

    public List<ItemStack> getOutputs(RandomSource random) {
        return this.outputs.stream().map(output -> output.roll(random)).toList();
    }

    @Override
    public @NotNull RecipeSerializer<? extends @NotNull Recipe<@NotNull RecipeInput>> getSerializer() {
        return PulverizerRecipeSerializer.INSTANCE;
    }

    @Override
    public @NotNull RecipeType<? extends @NotNull Recipe<@NotNull RecipeInput>> getType() {
        return TYPE;
    }

    public record ChanceOutput(ItemStackTemplate stack, int chance) {

        public static final int FULL_CHANCE = 10000;

        public static final Codec<ChanceOutput> CODEC = RecordCodecBuilder.create(
                builder -> builder
                        .group(
                                ItemStackTemplate.CODEC.fieldOf("item").forGetter(i -> i.stack),
                                Codec.DOUBLE.optionalFieldOf("chance", 1.0).xmap(ChanceOutput::normalizeChance, ChanceOutput::readableChance).forGetter(i -> i.chance)
                        ).apply(builder, ChanceOutput::new)
        );
        public static final StreamCodec<@NotNull RegistryFriendlyByteBuf, @NotNull ChanceOutput> STREAM_CODEC = StreamCodec.composite(
                ItemStackTemplate.STREAM_CODEC,
                ChanceOutput::stack,
                ByteBufCodecs.VAR_INT,
                ChanceOutput::chance,
                ChanceOutput::new
        );

        public ItemStack roll(RandomSource random) {
            if (this.chance >= FULL_CHANCE) {
                return this.stack.create();
            } else if (random.nextInt(FULL_CHANCE) < this.chance) {
                return this.stack.create();
            }
            return ItemStack.EMPTY;
        }

        public static ChanceOutput of(ItemStackTemplate stack, double chance) {
            return new ChanceOutput(stack, normalizeChance(chance));
        }

        public static int normalizeChance(double chance) {
            return (int) GlodUtil.clamp(chance * FULL_CHANCE, 0, FULL_CHANCE);
        }

        public static double readableChance(int chance) {
            return GlodUtil.clamp(chance / ((double) FULL_CHANCE), 0, 1);
        }

    }

}
