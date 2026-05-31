package com.glodblock.github.extendedae.common.me;

import appeng.menu.guisync.PacketWritable;
import com.glodblock.github.glodium.util.GlodCodecs;
import com.mojang.serialization.Codec;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public record DirectionSet(Set<Direction> backend) implements PacketWritable {

    public static final Codec<DirectionSet> CODEC = Codec
            .list(Direction.CODEC)
            .xmap(DirectionSet::new, DirectionSet::asList);
    public static final StreamCodec<@NotNull RegistryFriendlyByteBuf, @NotNull DirectionSet> STREAM_CODEC = GlodCodecs
            .list(Direction.STREAM_CODEC)
            .map(DirectionSet::new, DirectionSet::asList);

    public DirectionSet() {
        this(EnumSet.allOf(Direction.class));
    }

    public DirectionSet(Collection<Direction> init) {
        this(EnumSet.copyOf(init));
    }

    public DirectionSet(RegistryFriendlyByteBuf buf) {
        this(fromBytes(buf));
    }

    public void reload(Collection<Direction> sides) {
        this.backend.clear();
        this.backend.addAll(sides);
    }

    public Set<Direction> asSet() {
        return this.backend;
    }

    public List<Direction> asList() {
        return new ArrayList<>(this.backend);
    }

    public void load(ValueInput data, String name) {
        data.childrenList(name).ifPresent(list -> {
            this.backend.clear();
            for (var side : list) {
                side.getString("side").ifPresent(s -> this.backend.add(Direction.byName(s)));
            }
        });
    }

    public void save(ValueOutput data, String name) {
        var list = data.childrenList(name);
        for (var side : this.backend) {
            list.addChild().putString("side", side.name());
        }
    }

    private static List<Direction> fromBytes(RegistryFriendlyByteBuf buf) {
        List<Direction> fields = new ArrayList<>();
        int size = buf.readByte();
        while (size > 0) {
            size--;
            fields.add(Direction.from3DDataValue(buf.readByte()));
        }
        return fields;
    }

    @Override
    public void writeToPacket(RegistryFriendlyByteBuf buf) {
        buf.writeByte(this.backend.size());
        for (var side : this.backend) {
            buf.writeByte(side.get3DDataValue());
        }
    }

    public void clear() {
        this.backend.clear();
    }

}
