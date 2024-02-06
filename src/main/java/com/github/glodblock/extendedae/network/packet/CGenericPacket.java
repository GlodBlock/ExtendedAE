package com.github.glodblock.extendedae.network.packet;

import com.github.glodblock.extendedae.network.packet.sync.IActionHolder;
import com.github.glodblock.extendedae.network.packet.sync.ObjectSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public class CGenericPacket implements IMessage<CGenericPacket> {

    private String name;
    private Object[] paras;

    public CGenericPacket() {
        // NO-OP
    }

    public CGenericPacket(String name) {
        this.name = name;
        this.paras = null;
    }

    public CGenericPacket(String name, Object... paras) {
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
            ObjectSerializer.to(this.paras, buf);
        }
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        this.name = buf.readUtf();
        if (buf.readBoolean()) {
            this.paras = ObjectSerializer.from(buf);
        } else {
            this.paras = null;
        }
    }

    @Override
    public void onMessage(Player player) {
        if (player.containerMenu instanceof IActionHolder ah) {
            var fun = ah.getActionMap().get(this.name);
            if (fun != null) {
                fun.accept(this.paras);
            }
        }
    }

    @Override
    public Class<CGenericPacket> getPacketClass() {
        return CGenericPacket.class;
    }

    @Override
    public boolean isClient() {
        return false;
    }

}
