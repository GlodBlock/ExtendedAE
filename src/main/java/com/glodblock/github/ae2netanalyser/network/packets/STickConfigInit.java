package com.glodblock.github.ae2netanalyser.network.packets;

import com.glodblock.github.ae2netanalyser.AEAnalyser;
import com.glodblock.github.ae2netanalyser.client.gui.GuiProfiler;
import com.glodblock.github.ae2netanalyser.common.items.ItemTickAnalyzer;
import com.glodblock.github.glodium.network.packet.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class STickConfigInit implements IMessage {

    private ItemTickAnalyzer.TickConfig config;

    public STickConfigInit() {
        // NO-OP
    }

    public STickConfigInit(ItemTickAnalyzer.TickConfig config) {
        this.config = config;
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        this.config.writeToBytes(buf);
    }

    @Override
    public void fromBytes(RegistryFriendlyByteBuf buf) {
        this.config = ItemTickAnalyzer.TickConfig.readFromBytes(buf);
    }

    @Override
    public void onMessage(Player player) {
        if (Minecraft.getInstance().screen instanceof GuiProfiler gui) {
            gui.loadConfig(this.config);
        }
    }

    @Override
    public boolean isClient() {
        return true;
    }

    @Override
    public @NotNull Identifier id() {
        return AEAnalyser.id("tick_config_init");
    }

}