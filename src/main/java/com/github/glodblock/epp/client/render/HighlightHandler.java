package com.github.glodblock.epp.client.render;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.PriorityQueue;
import it.unimi.dsi.fastutil.objects.ObjectHeapPriorityQueue;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;

public class HighlightHandler {

    private static final PriorityQueue<HighlightData> BLOCK_QUEUE = new ObjectHeapPriorityQueue<>(Comparator.comparingLong(o -> o.time));
    private static final ObjectSet<HighlightData> BLOCKS = new ObjectOpenCustomHashSet<>(
            new Hash.Strategy<>() {
                @Override
                public int hashCode(HighlightData o) {
                    return o.dim.hashCode() ^ o.pos.hashCode();
                }

                @Override
                public boolean equals(HighlightData a, HighlightData b) {
                    return (a == b) || (a != null && b != null && Objects.equals(a.dim, b.dim) && Objects.equals(a.pos, b.pos) && dirCheck(a.face, b.face));
                }

                private static boolean dirCheck(Direction a, Direction b) {
                    if (a == null || b == null) {
                        return true;
                    }
                    return a == b;
                }

            }
    );

    public static void highlight(BlockPos pos, ResourceKey<Level> dim, long time) {
        highlight(pos, null, dim, time, new AABB(pos));
    }

    public static void highlight(BlockPos pos, Direction face, ResourceKey<Level> dim, long time, AABB box) {
        var r = new HighlightData(pos, face, time, dim, box);
        if (!BLOCKS.contains(r)) {
            BLOCK_QUEUE.enqueue(r);
            BLOCKS.add(r);
        }
    }


    public static void expire() {
        if (BLOCK_QUEUE.isEmpty()) {
            return;
        }
        BLOCKS.remove(BLOCK_QUEUE.first());
        BLOCK_QUEUE.dequeue();
    }

    public static HighlightData getFirst() {
        if (BLOCK_QUEUE.isEmpty()) {
            return null;
        }
        return BLOCK_QUEUE.first();
    }

    public static Collection<HighlightData> getBlockData() {
        return BLOCKS;
    }

    public record HighlightData(BlockPos pos, Direction face, long time, ResourceKey<Level> dim, AABB box) {

        public boolean checkDim(ResourceKey<Level> dim) {
            if (dim == null || this.dim == null) {
                return false;
            }
            return dim.equals(this.dim);
        }

    }

}
