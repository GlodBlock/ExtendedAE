package com.glodblock.github.appflux.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public class TileCache {

    private final ServerLevel level;
    private final BlockPos fromPos;

    private TileCache(ServerLevel level, BlockPos fromPos) {
        this.level = level;
        this.fromPos = fromPos;
    }

    public static TileCache create(ServerLevel level, BlockPos fromPos) {
        return new TileCache(level, fromPos);
    }

    @Nullable
    public BlockEntity find() {
        return level.getBlockEntity(fromPos);
    }

}