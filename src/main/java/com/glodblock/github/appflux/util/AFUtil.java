package com.glodblock.github.appflux.util;

import net.minecraft.world.item.ItemStack;
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

}
