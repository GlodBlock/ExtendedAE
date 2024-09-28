package com.glodblock.github.extendedae.api;

import appeng.api.config.CondenserOutput;
import appeng.core.definitions.AEItems;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.jetbrains.annotations.NotNull;

public enum VoidMode implements StringRepresentable {

    TRASH(Items.AIR),
    MATTER_BALLS(AEItems.MATTER_BALL),
    SINGULARITY(AEItems.SINGULARITY);

    public final ItemLike output;

    VoidMode(ItemLike output) {
        this.output = output;
    }

    public static final Codec<VoidMode> CODEC = StringRepresentable.fromEnum(VoidMode::values);
    public static final StreamCodec<RegistryFriendlyByteBuf, VoidMode> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(VoidMode.class);

    public int getPower() {
        return switch (this) {
            case TRASH -> CondenserOutput.TRASH.requiredPower;
            case MATTER_BALLS -> CondenserOutput.MATTER_BALLS.requiredPower;
            case SINGULARITY -> CondenserOutput.SINGULARITY.requiredPower;
        };
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name();
    }

}
