package com.github.glodblock.extendedae.network.packet;

import appeng.api.stacks.AEKey;
import appeng.client.render.crafting.AssemblerAnimationStatus;
import com.github.glodblock.extendedae.common.tileentities.TileExMolecularAssembler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SAssemblerAnimation implements IMessage<SAssemblerAnimation> {

    private BlockPos pos;
    private byte rate;
    private AEKey what;

    public SAssemblerAnimation() {
        // NO-OP
    }

    public SAssemblerAnimation(BlockPos pos, byte rate, AEKey what) {
        this.rate = rate;
        this.pos = pos;
        this.what = what;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeByte(this.rate);
        buf.writeVarLong(this.pos.asLong());
        AEKey.writeKey(buf, this.what);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        this.rate = buf.readByte();
        this.pos = BlockPos.of(buf.readVarLong());
        this.what = AEKey.readKey(buf);
    }

    @Override
    public void onMessage(Player player) {
        BlockEntity te = player.getCommandSenderWorld().getBlockEntity(this.pos);
        if (te instanceof TileExMolecularAssembler ma) {
            ma.setAnimationStatus(new AssemblerAnimationStatus(this.rate, this.what.wrapForDisplayOrFilter()));
        }
    }

    @Override
    public Class<SAssemblerAnimation> getPacketClass() {
        return SAssemblerAnimation.class;
    }

    @Override
    public boolean isClient() {
        return true;
    }
}
