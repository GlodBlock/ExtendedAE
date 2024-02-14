package com.glodblock.github.ae2netanalyser.common.me.p2p.p2pdata;

import appeng.parts.p2p.P2PTunnelPart;
import com.glodblock.github.ae2netanalyser.AEAnalyser;
import com.mojang.datafixers.util.Pair;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

public record P2PLocation(GlobalPos pos, Direction face) {

    public void writeToNbt(CompoundTag nbt) {
        GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, pos)
                .result()
                .ifPresent(tag -> nbt.put("pos", tag));
        nbt.putByte("face", (byte) face.ordinal());
    }

    public static P2PLocation of(@NotNull P2PTunnelPart<?> p2p) {
        var te = p2p.getBlockEntity();
        assert te.getLevel() != null;
        return new P2PLocation(
                GlobalPos.of(
                        te.getLevel().dimension(),
                        te.getBlockPos()
                ),
                p2p.getSide()
        );
    }

    public static P2PLocation readFromNbt(CompoundTag nbt) {
        return new P2PLocation(
                GlobalPos.CODEC.decode(NbtOps.INSTANCE, nbt.get("pos"))
                        .resultOrPartial(Util.prefix("P2P position", AEAnalyser.LOGGER::error))
                        .map(Pair::getFirst)
                        .orElse(null),
                Direction.values()[nbt.getByte("face")]
        );
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeResourceKey(pos.dimension());
        buf.writeLong(pos.pos().asLong());
        buf.writeByte(face.ordinal());
    }

    public static P2PLocation fromBytes(FriendlyByteBuf buf) {
        return new P2PLocation(
                GlobalPos.of(
                        buf.readResourceKey(Registries.DIMENSION),
                        BlockPos.of(buf.readLong())
                ),
                Direction.values()[buf.readByte()]
        );
    }

}
