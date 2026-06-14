package com.glodblock.github.appflux.network;

import com.glodblock.github.appflux.AppFlux;
import com.glodblock.github.glodium.network.packet.CGenericPacket;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class CAFGenericPacket extends CGenericPacket {

    public CAFGenericPacket() {
        // NO-OP
    }

    public CAFGenericPacket(String name) {
        super(name);
    }

    public CAFGenericPacket(String name, Object... paras) {
        super(name, paras);
    }

    @Override
    public @NotNull Identifier id() {
        return AppFlux.id("c_generic");
    }
}
