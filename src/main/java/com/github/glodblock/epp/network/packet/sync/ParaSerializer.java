package com.github.glodblock.epp.network.packet.sync;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;


public final class ParaSerializer {

    //////////////////////////////
    //                          //
    //     Serializer Zone      //
    //                          //
    //////////////////////////////

    public static void to(Object[] obj, FriendlyByteBuf buf) {
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
                buf.writeUtf(s, 1024);
            } else if (o instanceof ItemStack s) {
                buf.writeByte(PT.STACK.ordinal());
                buf.writeItemStack(s, true);
            } else {
                throw new IllegalArgumentException("Args contains invalid type: " + o.getClass().getName());
            }
        }
    }

    public static Object[] from(FriendlyByteBuf buf) {
        var objs = new Object[buf.readByte()];
        for (int i = 0; i < objs.length; i ++) {
            switch (PT.values()[buf.readByte()]) {
                case INT -> objs[i] = buf.readVarInt();
                case LONG -> objs[i] = buf.readVarLong();
                case SHORT -> objs[i] = buf.readShort();
                case BOOLEN -> objs[i] = buf.readBoolean();
                case STRING -> objs[i] = buf.readUtf(1024);
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
