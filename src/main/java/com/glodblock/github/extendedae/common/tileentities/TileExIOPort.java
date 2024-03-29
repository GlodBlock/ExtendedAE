package com.glodblock.github.extendedae.common.tileentities;

import appeng.blockentity.storage.IOPortBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TileExIOPort extends IOPortBlockEntity {

    public TileExIOPort(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);
    }
}
