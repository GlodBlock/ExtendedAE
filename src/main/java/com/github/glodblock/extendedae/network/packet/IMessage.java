package com.github.glodblock.extendedae.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public interface IMessage<MSG> {

    void toBytes(FriendlyByteBuf buf);

    void fromBytes(FriendlyByteBuf buf);

    void onMessage(Player player);

    Class<MSG> getPacketClass();

    boolean isClient();

}
