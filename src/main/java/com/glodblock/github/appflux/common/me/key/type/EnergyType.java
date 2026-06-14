package com.glodblock.github.appflux.common.me.key.type;

import com.glodblock.github.appflux.AppFlux;
import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum EnergyType implements StringRepresentable {

    FE("neoforge");

    private final String mod;
    public static final Codec<EnergyType> CODEC = StringRepresentable.fromEnum(EnergyType::values);

    EnergyType(String mod) {
        this.mod = mod;
    }

    public Identifier id() {
        return AppFlux.id(this.name().toLowerCase());
    }

    public String from() {
        return this.mod;
    }

    public Component translate() {
        return Component.translatable("appflux.type." + this.name().toLowerCase() + ".name");
    }

    public Identifier getIcon() {
        return AppFlux.id("energy/" + this.name().toLowerCase());
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name();
    }

}
