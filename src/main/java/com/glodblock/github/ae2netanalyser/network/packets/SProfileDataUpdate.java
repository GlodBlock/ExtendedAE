package com.glodblock.github.ae2netanalyser.network.packets;

import com.glodblock.github.ae2netanalyser.AEAnalyser;
import com.glodblock.github.ae2netanalyser.client.render.ProfileDataHandler;
import com.glodblock.github.ae2netanalyser.common.me.ticker.ProfileData;
import com.glodblock.github.glodium.network.packet.IMessage;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class SProfileDataUpdate implements IMessage {

    private ProfileData data;

    public SProfileDataUpdate() {
        // NO-OP
    }

    public SProfileDataUpdate(ProfileData data) {
        this.data = data;
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        this.data.writeBytes(buf);
    }

    @Override
    public void fromBytes(RegistryFriendlyByteBuf buf) {
        this.data = ProfileData.readBytes(buf);
    }

    @Override
    public void onMessage(Player player) {
        ProfileDataHandler.receiveData(this.data);
    }

    @Override
    public boolean isClient() {
        return true;
    }

    @Override
    public @NotNull Identifier id() {
        return AEAnalyser.id("profile_update");
    }
}
