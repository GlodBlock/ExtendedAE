package com.glodblock.github.extendedae.network.packet;

import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.client.gui.GuiExPatternTerminal;
import com.glodblock.github.glodium.network.packet.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SExPatternInfo implements IMessage {

    private long id;
    private BlockPos pos;
    private ResourceKey<Level> dim;
    @Nullable
    private Direction face;

    public SExPatternInfo() {
        // NO-OP
    }

    public SExPatternInfo(long id, BlockPos pos, ResourceKey<Level> dim, @Nullable Direction face) {
        this.id = id;
        this.pos = pos;
        this.dim = dim;
        this.face = face;
    }

    public SExPatternInfo(long id, BlockPos pos, ResourceKey<Level> dim) {
        this(id, pos, dim, null);
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeVarLong(this.id);
        buf.writeVarLong(this.pos.asLong());
        buf.writeResourceKey(this.dim);
        if (this.face == null) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);
            buf.writeEnum(this.face);
        }
    }

    @Override
    public void fromBytes(RegistryFriendlyByteBuf buf) {
        this.id = buf.readVarLong();
        this.pos = BlockPos.of(buf.readVarLong());
        this.dim = buf.readResourceKey(Registries.DIMENSION);
        if (buf.readBoolean()) {
            this.face = buf.readEnum(Direction.class);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onMessage(Player player) {
        if (Minecraft.getInstance().screen instanceof GuiExPatternTerminal<?> gui) {
            gui.postTileInfo(this.id, this.pos, this.dim, this.face);
        }
    }

    @Override
    public boolean isClient() {
        return true;
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ExtendedAE.id("s_ex_pattern_info");
    }

}
