package com.glodblock.github.ae2netanalyser.common.me;

import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.jetbrains.annotations.NotNull;

public enum AnalyserMode implements StringRepresentable {

    FULL, NODES, CHANNELS, NONUM, P2P;

    public static AnalyserMode byIndex(int index) {
        return AnalyserMode.values()[index];
    }

    public Component getTranslatedName() {
        return Component.translatable("gui.ae2netanalyser.network_analyser.mode." + this.name());
    }

    public static final Codec<AnalyserMode> CODEC = StringRepresentable.fromEnum(AnalyserMode::values);

    public static final StreamCodec<FriendlyByteBuf, AnalyserMode> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(AnalyserMode.class);

    @Override
    public @NotNull String getSerializedName() {
        return this.name();
    }

}
