package com.github.glodblock.epp.client.render;

import com.glodblock.github.glodium.client.render.ColorData;
import com.glodblock.github.glodium.client.render.highlight.HighlightHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class EAEHighlightHandler {

    private static final ColorData RED = new ColorData(1f, 0f, 0f);

    public static void highlight(BlockPos pos, ResourceKey<Level> dim, long time) {
        highlight(pos, null, dim, time, new AABB(pos));
    }

    public static void highlight(BlockPos pos, Direction face, ResourceKey<Level> dim, long time, AABB box) {
        HighlightHandler.highlight(pos, face, dim, time, box, RED, EAEHighlightHandler::blink);
    }

    private static boolean blink() {
        return ((System.currentTimeMillis() / 500) & 1) != 0;
    }

}
