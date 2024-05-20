package com.glodblock.github.extendedae.recipe.util;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class FluidIngredient implements Predicate<FluidStack> {

    private final List<Fluid> fluids = new ArrayList<>();
    protected final Value value;

    private static final Codec<Value> CODEC_VALUE = ExtraCodecs.xor(FluidValue.CODEC, TagValue.CODEC)
            .xmap(e -> e.map(a -> a, b -> b), v -> {
                if (v instanceof FluidValue f) {
                    return Either.left(f);
                } else if (v instanceof TagValue f) {
                    return Either.right(f);
                } else {
                    throw new IllegalArgumentException();
                }
            });
    public static final Codec<FluidIngredient> CODEC = RecordCodecBuilder.create(
        builder -> builder.group(
                CODEC_VALUE.fieldOf("fluid_ingredient").forGetter(fi -> fi.value)
        ).apply(builder, FluidIngredient::new)
    );

    public List<Fluid> getFluid() {
        return this.fluids;
    }

    public static FluidIngredient of(FriendlyByteBuf buff) {
        var type = buff.readByte();
        if (type == 0) {
            return new FluidIngredient(new FluidValue(buff.readFluidStack()));
        } else if (type == 1) {
            return new FluidIngredient(new TagValue(TagKey.create(Registries.FLUID, buff.readResourceLocation())));
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void to(FriendlyByteBuf buff) {
        if (this.value instanceof FluidValue f) {
            buff.writeByte(0);
            buff.writeFluidStack(f.fluid);
        } else if (this.value instanceof TagValue f) {
            buff.writeByte(1);
            buff.writeResourceLocation(f.fluid.location());
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public FluidIngredient(Value v) {
        this.value = v;
        if (v instanceof FluidValue fluid) {
            if (!fluid.fluid.isEmpty()) {
                this.fluids.add(fluid.fluid.getFluid());
            }
        } else if (v instanceof TagValue fluid) {
            for (var holder : BuiltInRegistries.FLUID.getTagOrEmpty(fluid.fluid)) {
                this.fluids.add(holder.value());
            }
        }
    }

    @Override
    public boolean test(FluidStack fluidStack) {
        if (this.fluids.isEmpty()) {
            return fluidStack.isEmpty();
        }
        if (fluidStack.isEmpty()) {
            return false;
        }
        var fluid = fluidStack.getFluid();
        for (var tf : this.fluids) {
            if (tf.isSame(fluid)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        if (this.value instanceof TagValue f) {
            return "tag: " + f.fluid;
        }
        if (this.value instanceof FluidValue f) {
            return "fluid: " + f.fluid;
        }
        return super.toString();
    }

    public interface Value {

    }

    public record TagValue(TagKey<Fluid> fluid) implements Value {
        public static final Codec<TagValue> CODEC = RecordCodecBuilder.create(
                builder -> builder.group(TagKey.codec(Registries.FLUID).fieldOf("tag").forGetter(TagValue::fluid)).apply(builder, TagValue::new)
        );
    }

    public record FluidValue(FluidStack fluid) implements Value {
        public static final Codec<FluidValue> CODEC = RecordCodecBuilder.create(
                builder -> builder.group(FluidStack.CODEC.fieldOf("fluid").forGetter(FluidValue::fluid)).apply(builder, FluidValue::new)
        );
    }

}