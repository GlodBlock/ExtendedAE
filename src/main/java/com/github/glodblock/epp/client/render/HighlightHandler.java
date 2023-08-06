package com.github.glodblock.epp.client.render;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class HighlightHandler {

    private static BlockPos pos = null;
    private static long expiredTime = -1;
    private static ResourceKey<Level> dim = null;

    public static void highlight(BlockPos pos, ResourceKey<Level> dim, long time) {
        HighlightHandler.pos = pos;
        HighlightHandler.expiredTime = time;
        HighlightHandler.dim = dim;
    }

    public static boolean checkDim(ResourceKey<Level> dim) {
        if (dim == null || HighlightHandler.dim == null) {
            return false;
        }
        return dim.equals(HighlightHandler.dim);
    }

    public static void expire() {
        HighlightHandler.pos = null;
        HighlightHandler.expiredTime = -1;
        HighlightHandler.dim = null;
    }

    public static BlockPos getPos() {
        return pos;
    }

    public static long getTime() {
        return expiredTime;
    }

}
