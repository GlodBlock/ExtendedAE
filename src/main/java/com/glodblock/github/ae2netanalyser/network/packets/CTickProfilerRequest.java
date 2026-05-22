package com.glodblock.github.ae2netanalyser.network.packets;

import com.glodblock.github.ae2netanalyser.AEAnalyser;
import com.glodblock.github.ae2netanalyser.common.me.ticker.RequestBox;
import com.glodblock.github.glodium.network.packet.IMessage;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class CTickProfilerRequest implements IMessage {

    int duration;

    public CTickProfilerRequest() {
        // NO-OP
    }

    public CTickProfilerRequest(int duration) {
        this.duration = duration;
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeInt(this.duration);
    }

    @Override
    public void fromBytes(RegistryFriendlyByteBuf buf) {
        this.duration = buf.readInt();
    }

    @Override
    public void onMessage(Player player) {
        if (this.duration <= 0) {
            if (RequestBox.cancelProfile(player)) {
                player.sendSystemMessage(Component.translatable("chat.ae2netanalyser.tick_analyser.cannel"));
            } else {
                player.sendSystemMessage(Component.translatable("chat.ae2netanalyser.tick_analyser.no_cannel"));
            }
        } else {
            switch (RequestBox.requestProfile(player, this.duration)) {
                case OK -> player.sendSystemMessage(Component.translatable("chat.ae2netanalyser.tick_analyser.begin", this.duration));
                case WAIT -> player.sendSystemMessage(Component.translatable("chat.ae2netanalyser.tick_analyser.waiting", this.duration));
                case DENY -> player.sendSystemMessage(Component.translatable("chat.ae2netanalyser.tick_analyser.user_control", this.duration));
            }
        }
    }

    @Override
    public boolean isClient() {
        return false;
    }

    @Override
    public @NotNull Identifier id() {
        return AEAnalyser.id("tick_profiler_request");
    }

}