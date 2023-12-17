package com.github.glodblock.epp.network.packet;

import com.github.glodblock.epp.network.packet.sync.IActionHolder;
import com.github.glodblock.epp.network.packet.sync.ParaSerializer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public class SGenericPacket implements IMessage<SGenericPacket> {

    private String name;
    private Object[] paras;

    public SGenericPacket() {
        // NO-OP
    }

    public SGenericPacket(String name) {
        this.name = name;
        this.paras = null;
    }

    public SGenericPacket(String name, Object... paras) {
        this.name = name;
        this.paras = paras;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(this.name);
        if (this.paras == null) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);
            ParaSerializer.to(this.paras, buf);
        }
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        this.name = buf.readUtf();
        if (buf.readBoolean()) {
            this.paras = ParaSerializer.from(buf);
        } else {
            this.paras = null;
        }
    }

    @Override
    public void onMessage(Player player) {
        if (Minecraft.getInstance().screen instanceof IActionHolder ah) {
            var fun = ah.getActionMap().get(this.name);
            if (fun != null) {
                fun.accept(this.paras);
            }
        }
    }

    @Override
    public Class<SGenericPacket> getPacketClass() {
        return SGenericPacket.class;
    }

    @Override
    public boolean isClient() {
        return true;
    }

}