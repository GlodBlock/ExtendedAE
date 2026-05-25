package com.glodblock.github.extendedae.network.packet;

import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.glodium.network.packet.CGenericPacket;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class CEAEGenericPacket extends CGenericPacket {

    public CEAEGenericPacket() {
        // NO-OP
    }

    public CEAEGenericPacket(String name) {
        super(name);
    }

    public CEAEGenericPacket(String name, Object... paras) {
        super(name, paras);
    }

    @Override
    public @NotNull Identifier id() {
        return ExtendedAE.id("c_generic");
    }
}
