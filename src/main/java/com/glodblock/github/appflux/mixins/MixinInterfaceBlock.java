package com.glodblock.github.appflux.mixins;

import appeng.block.AEBaseBlock;
import appeng.block.misc.InterfaceBlock;
import appeng.helpers.InterfaceLogicHost;
import com.glodblock.github.appflux.util.AFUtil;
import com.glodblock.github.appflux.util.helpers.INeighborListener;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(InterfaceBlock.class)
public abstract class MixinInterfaceBlock extends AEBaseBlock {

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Block block, @NotNull BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof InterfaceLogicHost te) {
            AFUtil.notifyNeighbor((INeighborListener) te.getInterfaceLogic(), pos, fromPos);
        }
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof InterfaceLogicHost te) {
            AFUtil.notifyNeighbor((INeighborListener) te.getInterfaceLogic(), pos, neighbor);
        }
    }

    protected MixinInterfaceBlock(Properties props) {
        super(props);
    }

}
