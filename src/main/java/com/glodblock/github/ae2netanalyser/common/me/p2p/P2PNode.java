package com.glodblock.github.ae2netanalyser.common.me.p2p;

import appeng.parts.p2p.MEP2PTunnelPart;
import appeng.parts.p2p.P2PTunnelPart;
import com.glodblock.github.ae2netanalyser.common.me.p2p.p2pdata.P2PData;
import com.glodblock.github.ae2netanalyser.common.me.p2p.p2pdata.P2PLocation;
import com.google.common.base.Objects;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

public record P2PNode(short freq, P2PLocation location, P2PData info) {

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeShort(freq);
        location.toBytes(buf);
        info.toBytes(buf);
    }

    public static P2PNode of(@NotNull P2PTunnelPart<?> p2p) {
        return new P2PNode(
                p2p.getFrequency(),
                P2PLocation.of(p2p),
                P2PData.builder(p2p)
                        .online(p2p.isActive())
                        .output(p2p.isOutput())
                        .custom(getExtraData(p2p))
                        .build()
        );
    }

    private static Object[] getExtraData(@NotNull P2PTunnelPart<?> p2p) {
        if (p2p instanceof MEP2PTunnelPart meP2P) {
            var channel = 0;
            var grid = meP2P.getExternalFacingNode();
            if (grid != null) {
                channel = grid.getUsedChannels();
            }
            return new Object[] {channel};
        }
        return null;
    }

    public static P2PNode fromBytes(FriendlyByteBuf buf) {
        return new P2PNode(
                buf.readShort(),
                P2PLocation.fromBytes(buf),
                P2PData.readBytes(buf)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(freq, location);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof P2PNode node) {
            if (node.freq != freq) {
                return false;
            }
            return node.location.equals(location);
        }
        return false;
    }
}
