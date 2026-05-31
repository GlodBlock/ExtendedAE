package com.glodblock.github.extendedae.common.tileentities.matrix;

import com.glodblock.github.extendedae.common.me.matrix.ClusterAssemblerMatrix;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TileAssemblerMatrixSpeed extends TileAssemblerMatrixFunction {

    public TileAssemblerMatrixSpeed(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public void add(ClusterAssemblerMatrix c) {
        c.addSpeedCore();
    }

}
