package com.glodblock.github.extendedae.network.packet;

import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.client.render.EAEHighlightHandler;
import com.glodblock.github.glodium.client.render.highlight.HighlightHandler;
import com.glodblock.github.glodium.network.packet.IMessage;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SHighlightRequest implements IMessage {

    private final List<HighlightHandler.HighlightData> data = new ArrayList<>();

    public SHighlightRequest() {
        // NO-OP
    }

    public SHighlightRequest(List<HighlightHandler.HighlightData> data) {
        this.data.addAll(data);
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeInt(data.size());
        for (var hd : this.data) {
            buf.writeBlockPos(hd.pos());
            buf.writeOptional(Optional.ofNullable(hd.face()), Direction.STREAM_CODEC);
            buf.writeResourceKey(hd.dim());
            buf.writeVec3(hd.box().getMaxPosition());
            buf.writeVec3(hd.box().getMinPosition());
        }
    }

    @Override
    public void fromBytes(RegistryFriendlyByteBuf buf) {
        var size = buf.readInt();
        for (int i = 0; i < size; i++) {
            var pos = buf.readBlockPos();
            var side = buf.readOptional(Direction.STREAM_CODEC).orElse(null);
            var dim = buf.readResourceKey(Registries.DIMENSION);
            var box = new AABB(buf.readVec3(), buf.readVec3());
            this.data.add(new HighlightHandler.HighlightData(pos, side, System.currentTimeMillis() + 8 * 1000, dim, box, null, null));
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onMessage(Player player) {
        for (var hd : this.data) {
            EAEHighlightHandler.highlight(hd.pos(), hd.face(), hd.dim(), hd.time(), hd.box());
        }
    }

    @Override
    public boolean isClient() {
        return true;
    }

    @Override
    public ResourceLocation id() {
        return ExtendedAE.id("highlight_request");
    }
}
