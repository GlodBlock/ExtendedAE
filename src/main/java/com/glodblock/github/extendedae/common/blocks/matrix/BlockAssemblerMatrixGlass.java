package com.glodblock.github.extendedae.common.blocks.matrix;

import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixGlass;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class BlockAssemblerMatrixGlass extends BlockAssemblerMatrixBase<TileAssemblerMatrixGlass> {

    public BlockAssemblerMatrixGlass() {
        super(glassProps().noOcclusion().isViewBlocking((a, b, c) -> false));
    }

    @Override
    public float getShadeBrightness(@NotNull BlockState state, @NotNull BlockGetter blockGetter, @NotNull BlockPos pos) {
        return 1f;
    }

    public boolean propagatesSkylightDown(@NotNull BlockState state, @NotNull BlockGetter blockGetter, @NotNull BlockPos p4) {
        return true;
    }

    @Override
    public Item getPresentItem() {
        return EAESingletons.ASSEMBLER_MATRIX_GLASS.asItem();
    }

    @Override
    protected @NotNull VoxelShape getVisualShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected boolean skipRendering(@NotNull BlockState state, @NotNull BlockState adjacentBlockState, @NotNull Direction side) {
        if (adjacentBlockState.is(this) && adjacentBlockState.getRenderShape() == state.getRenderShape()) {
            return true;
        }
        return super.skipRendering(state, adjacentBlockState, side);
    }

}