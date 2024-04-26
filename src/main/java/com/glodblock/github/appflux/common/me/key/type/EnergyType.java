package com.glodblock.github.appflux.common.me.key.type;

import com.glodblock.github.appflux.AppFlux;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public enum EnergyType {

    FE("neoforge"), GTEU("gtceu");

    private final String mod;

    EnergyType(String mod) {
        this.mod = mod;
    }

    public ResourceLocation id() {
        return AppFlux.id(this.name().toLowerCase());
    }

    public String from() {
        return this.mod;
    }

    public Component translate() {
        return Component.translatable("appflux.type." + this.name().toLowerCase() + ".name");
    }

    public ResourceLocation getIcon() {
        return AppFlux.id("energy/" + this.name().toLowerCase());
    }

}
