package com.github.glodblock.epp.util;

import com.github.glodblock.epp.EPP;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenCustomHashMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class FCUtil {

    private static final Object2ReferenceMap<Class<?>, BlockEntityType<? extends BlockEntity>> TILE_CACHE = new Object2ReferenceOpenCustomHashMap<>(HashUtil.CLASS);

    @SuppressWarnings("all")
    public static <T extends BlockEntity> BlockEntityType<T> getTileType(Class<T> clazz, BlockEntityType.BlockEntitySupplier<? extends T> supplier, Block block) {
        if (block == null) {
            return (BlockEntityType<T>) TILE_CACHE.get(clazz);
        }
        return (BlockEntityType<T>) TILE_CACHE.computeIfAbsent(
                clazz,
                k -> BlockEntityType.Builder.of(supplier, block).build(null)
        );
    }

}
