package com.glodblock.github.appflux.common.me.key.type;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import com.glodblock.github.appflux.AppFlux;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import com.glodblock.github.appflux.config.AFConfig;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class FluxKeyType extends AEKeyType {

    public static final FluxKeyType TYPE = new FluxKeyType();

    private FluxKeyType() {
        super(AppFlux.id("flux"), FluxKey.class, Component.translatable("appflux.key.flux"));
    }

    @Override
    public int getAmountPerByte() {
        return AFConfig.getFluxPerByte();
    }

    @Override
    public @Nullable AEKey readFromPacket(RegistryFriendlyByteBuf input) {
        var type = input.readEnum(EnergyType.class);
        return FluxKey.of(type);
    }

    @Override
    public MapCodec<? extends AEKey> codec() {
        return FluxKey.MAP_CODEC;
    }

    @Override
    public int getAmountPerOperation() {
        return 1024 * 16;
    }

}
