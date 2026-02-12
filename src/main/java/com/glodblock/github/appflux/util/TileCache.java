package com.glodblock.github.appflux.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;

public record TileCache(ServerLevel level, BlockPos fromPos) {

    public static TileCache create(ServerLevel level, BlockPos fromPos) {
        return new TileCache(level, fromPos);
    }

    @Nullable
    public BlockEntity find() {
        return level.getBlockEntity(fromPos);
    }

}
