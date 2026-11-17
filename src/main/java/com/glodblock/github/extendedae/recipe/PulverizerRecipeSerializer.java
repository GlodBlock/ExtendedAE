package com.glodblock.github.extendedae.recipe;

import com.glodblock.github.glodium.recipe.stack.IngredientStack;
import com.glodblock.github.glodium.util.GlodCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

public class PulverizerRecipeSerializer {

    public final static MapCodec<PulverizerRecipe> CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                    PulverizerRecipe.ChanceOutput.CODEC.listOf().fieldOf("outputs").forGetter(ir -> ir.outputs),
                    IngredientStack.ITEM_CODEC.fieldOf("input").forGetter(ir -> ir.input),
                    Codec.LONG.fieldOf("energy").forGetter(ir -> ir.energy)
            ).apply(builder, PulverizerRecipe::new)
    );
    public final static StreamCodec<@NotNull RegistryFriendlyByteBuf, @NotNull PulverizerRecipe> STREAM_CODEC = StreamCodec.composite(
            GlodCodecs.list(PulverizerRecipe.ChanceOutput.STREAM_CODEC),
            r -> r.outputs,
            IngredientStack.ITEM_STREAM_CODEC,
            r -> r.input,
            ByteBufCodecs.LONG,
            r -> r.energy,
            PulverizerRecipe::new
    );
    public final static RecipeSerializer<@NotNull PulverizerRecipe> INSTANCE = new RecipeSerializer<>(CODEC, STREAM_CODEC);

    private PulverizerRecipeSerializer() {
        // NO-OP
    }

}
