package com.glodblock.github.appflux.mixins.extendedae;

import appeng.block.AEBaseBlock;
import appeng.helpers.InterfaceLogicHost;
import com.glodblock.github.appflux.util.AFUtil;
import com.glodblock.github.appflux.util.helpers.INeighborListener;
import com.glodblock.github.extendedae.common.blocks.BlockExInterface;
import com.glodblock.github.extendedae.common.blocks.BlockOversizeInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({BlockExInterface.class, BlockOversizeInterface.class})
public abstract class MixinBlockExInterface extends AEBaseBlock {

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

    protected MixinBlockExInterface(Properties props) {
        super(props);
    }

}
