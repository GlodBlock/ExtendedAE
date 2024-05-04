package com.glodblock.github.ae2netanalyser.network.packets;

import com.glodblock.github.ae2netanalyser.AEAnalyser;
import com.glodblock.github.glodium.network.packet.CGenericPacket;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class CAnalyserGeneric extends CGenericPacket {

    public CAnalyserGeneric() {
        // NO-OP
    }

    public CAnalyserGeneric(String name) {
        super(name);
    }


    @Override
    public @NotNull ResourceLocation id() {
        return AEAnalyser.id("client_generic");
    }

}
