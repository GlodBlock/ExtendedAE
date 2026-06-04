package com.glodblock.github.extendedae.recipe;

import com.glodblock.github.glodium.recipe.stack.IngredientStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

public final class CircuitCutterRecipeSerializer {

    public final static MapCodec<CircuitCutterRecipe> CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                    ItemStackTemplate.CODEC.fieldOf("output").forGetter(ir -> ir.output),
                    IngredientStack.ITEM_CODEC.fieldOf("input").forGetter(ir -> ir.input),
                    Codec.LONG.fieldOf("energy").forGetter(ir -> ir.energy)
            ).apply(builder, CircuitCutterRecipe::new)
    );
    public final static StreamCodec<@NotNull RegistryFriendlyByteBuf, @NotNull CircuitCutterRecipe> STREAM_CODEC = StreamCodec.composite(
            ItemStackTemplate.STREAM_CODEC,
            r -> r.output,
            IngredientStack.ITEM_STREAM_CODEC,
            r -> r.input,
            ByteBufCodecs.LONG,
            r -> r.energy,
            CircuitCutterRecipe::new
    );
    public final static RecipeSerializer<@NotNull CircuitCutterRecipe> INSTANCE = new RecipeSerializer<>(CODEC, STREAM_CODEC);

    private CircuitCutterRecipeSerializer() {
        // NO-OP
    }

}
