package com.glodblock.github.ae2netanalyser.network.packets;

import com.glodblock.github.ae2netanalyser.common.items.ItemNetworkAnalyser;
import com.glodblock.github.ae2netanalyser.container.ContainerAnalyser;
import com.glodblock.github.glodium.network.packet.IMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public class CAnalyserConfigSave implements IMessage<CAnalyserConfigSave> {

    private ItemNetworkAnalyser.AnalyserConfig config;

    public CAnalyserConfigSave() {
        // NO-OP
    }

    public CAnalyserConfigSave(ItemNetworkAnalyser.AnalyserConfig config) {
        this.config = config;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        this.config.writeToBytes(buf);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        this.config = ItemNetworkAnalyser.AnalyserConfig.readFromBytes(buf);
    }

    @Override
    public void onMessage(Player player) {
        if (player.containerMenu instanceof ContainerAnalyser c) {
            c.saveConfig(this.config);
        }
    }

    @Override
    public Class<CAnalyserConfigSave> getPacketClass() {
        return CAnalyserConfigSave.class;
    }

    @Override
    public boolean isClient() {
        return false;
    }
}
