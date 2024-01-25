package com.glodblock.github.ae2netanalyser.network.packets;

import com.glodblock.github.ae2netanalyser.client.gui.GuiAnalyser;
import com.glodblock.github.ae2netanalyser.common.items.ItemNetworkAnalyser;
import com.glodblock.github.glodium.network.packet.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public class SAnalyserConfigInit implements IMessage<SAnalyserConfigInit> {

    private ItemNetworkAnalyser.AnalyserConfig config;

    public SAnalyserConfigInit() {
        // NO-OP
    }

    public SAnalyserConfigInit(ItemNetworkAnalyser.AnalyserConfig config) {
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
        if (Minecraft.getInstance().screen instanceof GuiAnalyser gui) {
            gui.loadConfig(this.config);
        }
    }

    @Override
    public Class<SAnalyserConfigInit> getPacketClass() {
        return SAnalyserConfigInit.class;
    }

    @Override
    public boolean isClient() {
        return true;
    }

}
