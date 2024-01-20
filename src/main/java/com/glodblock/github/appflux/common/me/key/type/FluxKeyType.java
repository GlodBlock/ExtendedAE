package com.glodblock.github.appflux.common.me.key.type;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import com.glodblock.github.appflux.AppFlux;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class FluxKeyType extends AEKeyType {

    public static final FluxKeyType TYPE = new FluxKeyType();

    private FluxKeyType() {
        super(AppFlux.id("flux"), FluxKey.class, Component.translatable("appflux.key.flux"));
    }

    @Override
    public @Nullable AEKey readFromPacket(FriendlyByteBuf input) {
        var type = input.readEnum(EnergyType.class);
        return FluxKey.of(type);
    }

    @Override
    public @Nullable AEKey loadKeyFromTag(CompoundTag tag) {
        return FluxKey.of(EnergyType.valueOf(tag.getString("t")));
    }

    @Override
    public int getAmountPerByte() {
        return 1024 * 64;
    }

    @Override
    public int getAmountPerOperation() {
        return 1024 * 16;
    }

}
