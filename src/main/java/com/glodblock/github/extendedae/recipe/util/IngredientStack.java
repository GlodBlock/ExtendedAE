package com.glodblock.github.extendedae.recipe.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.function.Predicate;

public abstract class IngredientStack<T> {

    protected final Predicate<T> ingredient;
    protected int amount;

    public static final Codec<Item> ITEM_CODEC = RecordCodecBuilder.create(
            builder -> builder
                    .group(
                            Ingredient.CODEC.fieldOf("ingredient").forGetter(i -> (Ingredient) i.ingredient),
                            ExtraCodecs.strictOptionalField(ExtraCodecs.POSITIVE_INT, "amount", 1).forGetter(i -> i.amount)
                    ).apply(builder, Item::new)
    );
    public static final Codec<Fluid> FLUID_CODEC = RecordCodecBuilder.create(
            builder -> builder
                    .group(
                            FluidIngredient.CODEC.fieldOf("ingredient").forGetter(i -> (FluidIngredient) i.ingredient),
                            ExtraCodecs.strictOptionalField(ExtraCodecs.POSITIVE_INT, "amount", 1).forGetter(i -> i.amount)
                    ).apply(builder, Fluid::new)
    );

    public IngredientStack(Predicate<T> ingredient, int amount) {
        this.ingredient = ingredient;
        this.amount = amount;
    }

    public Predicate<T> getIngredient() {
        return this.ingredient;
    }

    public int getAmount() {
        return this.amount;
    }

    public static IngredientStack.Item of(ItemStack ingredient) {
        return new Item(Ingredient.of(ingredient), ingredient.getCount());
    }

    public static IngredientStack.Item of(Ingredient ingredient, int amount) {
        return new Item(ingredient, amount);
    }

    public static IngredientStack.Fluid of(FluidStack ingredient) {
        return new Fluid(new FluidIngredient(new FluidIngredient.FluidValue(ingredient)), ingredient.getAmount());
    }

    public static IngredientStack.Fluid of(FluidIngredient ingredient, int amount) {
        return new Fluid(ingredient, amount);
    }

    public static IngredientStack.Item ofItem(FriendlyByteBuf buff) {
        return new Item(Ingredient.fromNetwork(buff), buff.readInt());
    }

    public static IngredientStack.Fluid ofFluid(FriendlyByteBuf buff) {
        return new Fluid(FluidIngredient.of(buff), buff.readInt());
    }

    public abstract void to(FriendlyByteBuf buff);

    @SuppressWarnings("unchecked")
    public void consume(Object stack) {
        if (this.amount <= 0) {
            return;
        }
        if (this.ingredient.test((T) stack)) {
            int from = getStackAmount((T) stack);
            if (from > this.amount) {
                this.setStackAmount((T) stack, from - this.amount);
                this.amount = 0;

            } else {
                this.setStackAmount((T) stack, 0);
                this.amount -= from;
            }
        }
    }

    public boolean isEmpty() {
        return this.amount <= 0;
    }

    public abstract boolean checkType(Object obj);

    public abstract IngredientStack<T> sample();

    public abstract int getStackAmount(T stack);

    public abstract void setStackAmount(T stack, int amount);

    @Override
    public String toString() {
        return this.amount + " " +this.ingredient;
    }

    public static final class Item extends IngredientStack<ItemStack> {

        public static final Item EMPTY = new Item(Ingredient.EMPTY, 0);

        private Item(Ingredient ingredient, int amount) {
            super(ingredient, amount);
        }

        @Override
        public void to(FriendlyByteBuf buff) {
            ((Ingredient) this.ingredient).toNetwork(buff);
            buff.writeInt(this.amount);
        }

        @Override
        public boolean checkType(Object obj) {
            return obj instanceof ItemStack;
        }

        @Override
        public Item sample() {
            return new Item((Ingredient) this.ingredient, this.amount);
        }

        @Override
        public int getStackAmount(ItemStack stack) {
            return stack.getCount();
        }

        @Override
        public void setStackAmount(ItemStack stack, int amount) {
            stack.setCount(amount);
        }

    }

    public static final class Fluid extends IngredientStack<FluidStack> {

        public static final Fluid EMPTY = new Fluid(new FluidIngredient(new FluidIngredient.FluidValue(FluidStack.EMPTY)), 0);

        private Fluid(FluidIngredient ingredient, int amount) {
            super(ingredient, amount);
        }

        @Override
        public void to(FriendlyByteBuf buff) {
            ((FluidIngredient) this.ingredient).to(buff);
            buff.writeInt(this.amount);
        }

        @Override
        public boolean checkType(Object obj) {
            return obj instanceof FluidStack;
        }

        @Override
        public IngredientStack<FluidStack> sample() {
            return new Fluid((FluidIngredient) this.ingredient, this.amount);
        }

        @Override
        public int getStackAmount(FluidStack stack) {
            return stack.getAmount();
        }

        @Override
        public void setStackAmount(FluidStack stack, int amount) {
            stack.setAmount(amount);
        }
    }

}
