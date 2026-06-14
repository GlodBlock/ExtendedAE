package com.glodblock.github.appflux.network;

import com.glodblock.github.appflux.AppFlux;
import com.glodblock.github.glodium.network.packet.SGenericPacket;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class SAFGenericPacket extends SGenericPacket {

    public SAFGenericPacket() {
        // NO-OP
    }

    public SAFGenericPacket(String name) {
        super(name);
    }

    public SAFGenericPacket(String name, Object... paras) {
        super(name, paras);
    }

    @Override
    public @NotNull Identifier id() {
        return AppFlux.id("s_generic");
    }

}
