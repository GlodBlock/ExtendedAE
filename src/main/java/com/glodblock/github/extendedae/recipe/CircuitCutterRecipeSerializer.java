package com.glodblock.github.extendedae.recipe;

import com.glodblock.github.extendedae.recipe.util.IngredientStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

public class CircuitCutterRecipeSerializer implements RecipeSerializer<CircuitCutterRecipe> {

    public final static CircuitCutterRecipeSerializer INSTANCE = new CircuitCutterRecipeSerializer();
    public final static Codec<CircuitCutterRecipe> CODEC = RecordCodecBuilder.create(
            builder -> builder.group(
                    ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf("output").forGetter(ir -> ir.output),
                    IngredientStack.ITEM_CODEC.fieldOf("input").forGetter(ir -> ir.input)
            ).apply(builder, CircuitCutterRecipe::new)
    );

    private CircuitCutterRecipeSerializer() {
        // NO-OP
    }

    @Override
    public @NotNull Codec<CircuitCutterRecipe> codec() {
        return CODEC;
    }

    @Override
    public @NotNull CircuitCutterRecipe fromNetwork(@NotNull FriendlyByteBuf buffer) {
        return new CircuitCutterRecipe(buffer.readItem(), IngredientStack.ofItem(buffer));
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull CircuitCutterRecipe recipe) {
        buffer.writeItem(recipe.output);
        recipe.input.to(buffer);
    }

}
