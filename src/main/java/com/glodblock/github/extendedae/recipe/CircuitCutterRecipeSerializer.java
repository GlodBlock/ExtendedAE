package com.glodblock.github.extendedae.recipe;

import com.glodblock.github.glodium.recipe.stack.IngredientStack;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

public class CircuitCutterRecipeSerializer implements RecipeSerializer<CircuitCutterRecipe> {

    public final static CircuitCutterRecipeSerializer INSTANCE = new CircuitCutterRecipeSerializer();
    public final static MapCodec<CircuitCutterRecipe> CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                    ItemStack.CODEC.fieldOf("output").forGetter(ir -> ir.output),
                    IngredientStack.ITEM_CODEC.fieldOf("input").forGetter(ir -> ir.input)
            ).apply(builder, CircuitCutterRecipe::new)
    );
    public final static StreamCodec<RegistryFriendlyByteBuf, CircuitCutterRecipe> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC,
            r -> r.output,
            IngredientStack.ITEM_STREAM_CODEC,
            r -> r.input,
            CircuitCutterRecipe::new
    );

    private CircuitCutterRecipeSerializer() {
        // NO-OP
    }

    @Override
    public @NotNull MapCodec<CircuitCutterRecipe> codec() {
        return CODEC;
    }

    @Override
    public @NotNull StreamCodec<RegistryFriendlyByteBuf, CircuitCutterRecipe> streamCodec() {
        return STREAM_CODEC;
    }

}
