package com.glodblock.github.extendedae.network.packet;

import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.glodium.network.packet.SGenericPacket;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class SEAEGenericPacket extends SGenericPacket {

    public SEAEGenericPacket() {
        // NO-OP
    }

    public SEAEGenericPacket(String name) {
        super(name);
    }

    public SEAEGenericPacket(String name, Object... paras) {
        super(name, paras);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ExtendedAE.id("s_generic");
    }

}
