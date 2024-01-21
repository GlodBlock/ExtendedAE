package com.glodblock.github.appflux.util;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.Nullable;

public class AFUtil {

    public static int clampLong(long value) {
        return (int) Math.min(value, Integer.MAX_VALUE);
    }

    @Nullable
    public static <T> T findCapability(ItemStack stack, Capability<T> capability) {
        if (!stack.isEmpty()) {
            return stack.getCapability(capability).resolve().orElse(null);
        }
        return null;
    }

    @Nullable
    public static <T> T findCapability(BlockEntity tile, Direction side, Capability<T> capability) {
        if (tile != null) {
            return tile.getCapability(capability, side).resolve().orElse(null);
        }
        return null;
    }

}
