package com.glodblock.github.ae2netanalyser.network.packets;

import com.glodblock.github.ae2netanalyser.client.render.NetworkDataHandler;
import com.glodblock.github.ae2netanalyser.common.me.NetworkData;
import com.glodblock.github.glodium.network.packet.IMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public class SNetworkDataUpdate implements IMessage<SNetworkDataUpdate> {

    private NetworkData data;

    public SNetworkDataUpdate() {
        // NO-OP
    }

    public SNetworkDataUpdate(NetworkData data) {
        this.data = data;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        this.data.writeBytes(buf);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        this.data = NetworkData.readBytes(buf);
    }

    @Override
    public void onMessage(Player player) {
        NetworkDataHandler.receiveData(this.data);
    }

    @Override
    public Class<SNetworkDataUpdate> getPacketClass() {
        return SNetworkDataUpdate.class;
    }

    @Override
    public boolean isClient() {
        return true;
    }
}
