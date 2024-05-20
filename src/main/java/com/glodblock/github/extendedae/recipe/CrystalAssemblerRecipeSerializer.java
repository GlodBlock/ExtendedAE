package com.glodblock.github.extendedae.recipe;

import com.glodblock.github.extendedae.recipe.util.IngredientStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CrystalAssemblerRecipeSerializer implements RecipeSerializer<CrystalAssemblerRecipe> {

    public final static CrystalAssemblerRecipeSerializer INSTANCE = new CrystalAssemblerRecipeSerializer();
    public final static Codec<CrystalAssemblerRecipe> CODEC = RecordCodecBuilder.create(
            builder -> builder.group(
                    ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf("output").forGetter(ir -> ir.output),
                    IngredientStack.ITEM_CODEC.listOf().fieldOf("input_items").forGetter(ir -> ir.inputs),
                    IngredientStack.FLUID_CODEC.optionalFieldOf("input_fluid").forGetter(ir -> ir.fluid)
            ).apply(builder, CrystalAssemblerRecipe::new)
    );

    private CrystalAssemblerRecipeSerializer() {
        // NO-OP
    }

    @Override
    public @NotNull Codec<CrystalAssemblerRecipe> codec() {
        return CODEC;
    }

    @Override
    public @NotNull CrystalAssemblerRecipe fromNetwork(@NotNull FriendlyByteBuf buffer) {
        ItemStack output = buffer.readItem();
        int inputSize = buffer.readByte();
        List<IngredientStack.Item> inputs = new ArrayList<>();
        for (int i = 0; i < inputSize; i ++) {
            inputs.add(IngredientStack.ofItem(buffer));
        }
        IngredientStack.Fluid fluid = null;
        if (buffer.readBoolean()) {
            fluid = IngredientStack.ofFluid(buffer);
        }
        return new CrystalAssemblerRecipe(output, inputs, fluid);
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull CrystalAssemblerRecipe recipe) {
        buffer.writeItem(recipe.output);
        buffer.writeByte(recipe.inputs.size());
        for (var is : recipe.inputs) {
            is.to(buffer);
        }
        buffer.writeBoolean(recipe.fluid.isPresent());
        recipe.fluid.ifPresent(f -> f.to(buffer));
    }

}
