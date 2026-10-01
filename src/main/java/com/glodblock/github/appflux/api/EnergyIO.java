package com.glodblock.github.appflux.api;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum EnergyIO implements StringRepresentable {

    INPUT,
    OUTPUT,
    BOTH;

    public static final Codec<EnergyIO> CODEC = StringRepresentable.fromEnum(EnergyIO::values);

    public static EnergyIO byId(int ordinal) {
        return switch (ordinal) {
            case 0 -> INPUT;
            case 1 -> OUTPUT;
            case 2 -> BOTH;
            default -> throw new IllegalArgumentException("Invalid ordinal");
        };
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name().toLowerCase(Locale.US);
    }

    public boolean isInput() {
        return this != OUTPUT;
    }

    public boolean isOutput() {
        return this != INPUT;
    }

}
