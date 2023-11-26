package com.github.glodblock.extendedae.network.packet;

import com.github.glodblock.extendedae.network.packet.sync.IActionHolder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

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
            to(this.paras, buf);
        }
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        this.name = buf.readUtf();
        if (buf.readBoolean()) {
            this.paras = from(buf);
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

    //////////////////////////////
    //                          //
    //     Serializer Zone      //
    //                          //
    //////////////////////////////

    private static void to(Object[] obj, FriendlyByteBuf buf) {
        buf.writeByte(obj.length);
        for (var o : obj) {
            if (o instanceof Integer i) {
                buf.writeByte(PT.INT.ordinal());
                buf.writeVarInt(i);
            } else if (o instanceof Long l) {
                buf.writeByte(PT.LONG.ordinal());
                buf.writeVarLong(l);
            } else if (o instanceof Short s) {
                buf.writeByte(PT.SHORT.ordinal());
                buf.writeShort(s);
            } else if (o instanceof Boolean b) {
                buf.writeByte(PT.BOOLEN.ordinal());
                buf.writeBoolean(b);
            } else if (o instanceof String s) {
                buf.writeByte(PT.STRING.ordinal());
                buf.writeUtf(s, 256);
            } else if (o instanceof ItemStack s) {
                buf.writeByte(PT.STACK.ordinal());
                buf.writeItem(s);
            } else {
                throw new IllegalArgumentException("Args contains invalid type: " + o.getClass().getName());
            }
        }
    }

    private static Object[] from(FriendlyByteBuf buf) {
        var objs = new Object[buf.readByte()];
        for (int i = 0; i < objs.length; i ++) {
            switch (PT.values()[buf.readByte()]) {
                case INT -> objs[i] = buf.readVarInt();
                case LONG -> objs[i] = buf.readVarLong();
                case SHORT -> objs[i] = buf.readShort();
                case BOOLEN -> objs[i] = buf.readBoolean();
                case STRING -> objs[i] = buf.readUtf(256);
                case STACK -> objs[i] = buf.readItem();
                default -> throw new IllegalArgumentException("Args contains unknown type.");
            }
        }
        return objs;
    }

    private enum PT {
        INT,
        LONG,
        SHORT,
        BOOLEN,
        STRING,
        STACK
    }

}
