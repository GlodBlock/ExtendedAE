package com.glodblock.github.ae2netanalyser.common.me.p2p.p2pdata;

import appeng.api.parts.IPartItem;
import appeng.parts.p2p.P2PTunnelPart;
import com.glodblock.github.glodium.network.packet.sync.ParaSerializer;
import com.glodblock.github.glodium.network.packet.sync.Paras;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class P2PData {

    @NotNull
    private final ResourceLocation type;
    private boolean isOutput = false;
    private boolean isOnline = false;
    @NotNull
    private String name = "";
    @Nullable
    private Object[] customData = null;

    private P2PData(@NotNull ResourceLocation type) {
        this.type = type;
    }

    public @NotNull ResourceLocation getType() {
        return this.type;
    }

    public boolean isOutput() {
        return this.isOutput;
    }

    public boolean isOnline() {
        return this.isOnline;
    }

    public @NotNull String getName() {
        return this.name;
    }

    public @NotNull Paras getCustomData() {
        return new Paras(this.customData);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeResourceLocation(this.type);
        buf.writeBoolean(this.isOutput);
        buf.writeBoolean(this.isOnline);
        buf.writeUtf(this.name);
        if (this.customData != null) {
            buf.writeBoolean(true);
            ParaSerializer.to(this.customData, buf);
        } else {
            buf.writeBoolean(false);
        }
    }

    public static P2PData readBytes(FriendlyByteBuf buf) {
        var info = new P2PData(buf.readResourceLocation());
        info.isOutput = buf.readBoolean();
        info.isOnline = buf.readBoolean();
        info.name = buf.readUtf();
        if (buf.readBoolean()) {
            info.customData = ParaSerializer.from(buf);
        }
        return info;
    }

    public static P2PInfoBuilder builder(@NotNull P2PTunnelPart<?> p2p) {
        return new P2PInfoBuilder(p2p);
    }

    public static class P2PInfoBuilder {

        private final P2PData data;

        private P2PInfoBuilder(@NotNull P2PTunnelPart<?> p2p) {
            this.data = new P2PData(IPartItem.getId(p2p.getPartItem()));
            this.data.name = p2p.getName().getString();
        }

        public P2PInfoBuilder online(boolean value) {
            this.data.isOnline = value;
            return this;
        }

        public P2PInfoBuilder output(boolean value) {
            this.data.isOutput = value;
            return this;
        }

        public P2PInfoBuilder name(@NotNull String value) {
            this.data.name = value;
            return this;
        }

        public P2PInfoBuilder custom(Object[] value) {
            this.data.customData = value;
            return this;
        }

        public P2PData build() {
            return this.data;
        }

    }

}
