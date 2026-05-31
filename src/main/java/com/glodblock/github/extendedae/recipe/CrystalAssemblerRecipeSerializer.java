package com.glodblock.github.extendedae.recipe;

import com.glodblock.github.glodium.recipe.stack.IngredientStack;
import com.glodblock.github.glodium.util.GlodCodecs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

public final class CrystalAssemblerRecipeSerializer {

    public final static MapCodec<CrystalAssemblerRecipe> CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                    ItemStackTemplate.CODEC.fieldOf("output").forGetter(ir -> ir.output),
                    IngredientStack.ITEM_CODEC.listOf().fieldOf("input_items").forGetter(ir -> ir.inputs),
                    IngredientStack.FLUID_CODEC.optionalFieldOf("input_fluid").forGetter(ir -> ir.fluid)
            ).apply(builder, CrystalAssemblerRecipe::new)
    );
    public final static StreamCodec<@NotNull RegistryFriendlyByteBuf, @NotNull CrystalAssemblerRecipe> STREAM_CODEC = StreamCodec.composite(
            ItemStackTemplate.STREAM_CODEC,
            r -> r.output,
            GlodCodecs.list(IngredientStack.ITEM_STREAM_CODEC),
            r -> r.inputs,
            GlodCodecs.optional(IngredientStack.FLUID_STREAM_CODEC),
            r -> r.fluid,
            CrystalAssemblerRecipe::new
    );
    public final static RecipeSerializer<@NotNull CrystalAssemblerRecipe> INSTANCE = new RecipeSerializer<>(CODEC, STREAM_CODEC);

    private CrystalAssemblerRecipeSerializer() {
        // NO-OP
    }

}
