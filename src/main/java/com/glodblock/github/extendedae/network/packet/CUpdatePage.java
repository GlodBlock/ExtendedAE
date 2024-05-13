package com.glodblock.github.extendedae.network.packet;

import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.api.IPage;
import com.glodblock.github.glodium.network.packet.IMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class CUpdatePage implements IMessage {

    private int page;

    public CUpdatePage() {
        // NO-OP
    }

    public CUpdatePage(int page) {
        this.page = page;
    }

    public CUpdatePage(Supplier<Integer> page) {
        this.page = page.get();
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeVarInt(this.page);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        this.page = buf.readVarInt();
    }

    @Override
    public void onMessage(Player player) {
        if (player.containerMenu instanceof IPage pg) {
            pg.setPage(this.page);
        }
    }

    @Override
    public boolean isClient() {
        return false;
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ExtendedAE.id("c_update_page");
    }

}
