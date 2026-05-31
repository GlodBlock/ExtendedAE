package com.glodblock.github.extendedae.recipe;

import com.glodblock.github.glodium.recipe.stack.IngredientStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

public final class CrystalFixerRecipeSerializer {

    public final static MapCodec<CrystalFixerRecipe> CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                    ItemStackTemplate.CODEC.fieldOf("input").forGetter(ir -> new ItemStackTemplate(ir.input.asItem())),
                    ItemStackTemplate.CODEC.fieldOf("output").forGetter(ir -> new ItemStackTemplate(ir.output.asItem())),
                    IngredientStack.ITEM_CODEC.fieldOf("fuel").forGetter(ir -> ir.fuel),
                    Codec.intRange(1, CrystalFixerRecipe.FULL_CHANCE).fieldOf("chance").forGetter(ir -> ir.chance)
            ).apply(builder, CrystalFixerRecipe::new)
    );
    public final static StreamCodec<@NotNull RegistryFriendlyByteBuf, @NotNull CrystalFixerRecipe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(Registries.BLOCK),
            r -> r.input,
            ByteBufCodecs.registry(Registries.BLOCK),
            r -> r.output,
            IngredientStack.ITEM_STREAM_CODEC,
            r -> r.fuel,
            ByteBufCodecs.INT,
            r -> r.chance,
            CrystalFixerRecipe::new
    );
    public final static RecipeSerializer<@NotNull CrystalFixerRecipe> INSTANCE = new RecipeSerializer<>(CODEC, STREAM_CODEC);

    private CrystalFixerRecipeSerializer() {
        // NO-OP
    }

}