package com.glodblock.github.appflux.common.blocks;

import appeng.block.AEBaseEntityBlock;
import com.glodblock.github.appflux.common.tileentities.TileFluxAccessor;
import com.glodblock.github.appflux.util.AFUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("deprecation")
public class BlockFluxAccessor extends AEBaseEntityBlock<TileFluxAccessor> {

    public BlockFluxAccessor() {
        super(metalProps());
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable("block.appflux.flux_accessor.tooltip.1").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("block.appflux.flux_accessor.tooltip.2").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Block block, @NotNull BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof TileFluxAccessor te) {
            Direction dir = AFUtil.getBlockDirection(pos, fromPos);
            if (dir != null) {
                te.invalidateAction(dir);
            }
        }
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof TileFluxAccessor te) {
            Direction dir = AFUtil.getBlockDirection(pos, neighbor);
            if (dir != null) {
                te.invalidateAction(dir);
            }
        }
    }

}
