package com.github.glodblock.epp.network.packet;

import com.github.glodblock.epp.api.IPage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public class CUpdatePage implements IMessage<CUpdatePage> {

    private int page;

    public CUpdatePage() {
        // NO-OP
    }

    public CUpdatePage(int page) {
        this.page = page;
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
    public Class<CUpdatePage> getPacketClass() {
        return CUpdatePage.class;
    }

    @Override
    public boolean isClient() {
        return false;
    }
}
