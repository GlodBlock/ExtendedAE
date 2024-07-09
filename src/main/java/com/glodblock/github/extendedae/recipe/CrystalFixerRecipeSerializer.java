package com.glodblock.github.extendedae.recipe;

import com.glodblock.github.glodium.recipe.stack.IngredientStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

public class CrystalFixerRecipeSerializer implements RecipeSerializer<CrystalFixerRecipe> {

    public final static CrystalFixerRecipeSerializer INSTANCE = new CrystalFixerRecipeSerializer();
    public final static MapCodec<CrystalFixerRecipe> CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                    ItemStack.CODEC.fieldOf("input").forGetter(ir -> new ItemStack(ir.input)),
                    ItemStack.CODEC.fieldOf("output").forGetter(ir -> new ItemStack(ir.output)),
                    IngredientStack.ITEM_CODEC.fieldOf("fuel").forGetter(ir -> ir.fuel),
                    Codec.intRange(1, CrystalFixerRecipe.FULL_CHANCE).fieldOf("chance").forGetter(ir -> ir.chance)
            ).apply(builder, CrystalFixerRecipe::new)
    );
    public final static StreamCodec<RegistryFriendlyByteBuf, CrystalFixerRecipe> STREAM_CODEC = StreamCodec.composite(
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

    private CrystalFixerRecipeSerializer() {
        // NO-OP
    }

    @Override
    public @NotNull MapCodec<CrystalFixerRecipe> codec() {
        return CODEC;
    }

    @Override
    public @NotNull StreamCodec<RegistryFriendlyByteBuf, CrystalFixerRecipe> streamCodec() {
        return STREAM_CODEC;
    }

}