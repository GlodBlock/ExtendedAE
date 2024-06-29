package com.glodblock.github.ae2netanalyser.network.packets;

import com.glodblock.github.ae2netanalyser.AEAnalyser;
import com.glodblock.github.ae2netanalyser.client.render.NetworkDataHandler;
import com.glodblock.github.ae2netanalyser.common.me.NetworkData;
import com.glodblock.github.glodium.network.packet.IMessage;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class SNetworkDataUpdate implements IMessage {

    private NetworkData data;

    public SNetworkDataUpdate() {
        // NO-OP
    }

    public SNetworkDataUpdate(NetworkData data) {
        this.data = data;
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        this.data.writeBytes(buf);
    }

    @Override
    public void fromBytes(RegistryFriendlyByteBuf buf) {
        this.data = NetworkData.readBytes(buf);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onMessage(Player player) {
        NetworkDataHandler.receiveData(this.data);
    }

    @Override
    public boolean isClient() {
        return true;
    }

    @Override
    public @NotNull ResourceLocation id() {
        return AEAnalyser.id("data_update");
    }
}
