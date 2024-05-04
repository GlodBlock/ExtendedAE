package com.glodblock.github.ae2netanalyser.network.packets;

import com.glodblock.github.ae2netanalyser.AEAnalyser;
import com.glodblock.github.ae2netanalyser.common.items.ItemNetworkAnalyzer;
import com.glodblock.github.ae2netanalyser.container.ContainerAnalyser;
import com.glodblock.github.glodium.network.packet.IMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class CAnalyserConfigSave implements IMessage {

    private ItemNetworkAnalyzer.AnalyserConfig config;

    public CAnalyserConfigSave() {
        // NO-OP
    }

    public CAnalyserConfigSave(ItemNetworkAnalyzer.AnalyserConfig config) {
        this.config = config;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        this.config.writeToBytes(buf);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        this.config = ItemNetworkAnalyzer.AnalyserConfig.readFromBytes(buf);
    }

    @Override
    public void onMessage(Player player) {
        if (player.containerMenu instanceof ContainerAnalyser c) {
            c.saveConfig(this.config);
        }
    }

    @Override
    public boolean isClient() {
        return false;
    }

    @Override
    public @NotNull ResourceLocation id() {
        return AEAnalyser.id("config_save");
    }
}
