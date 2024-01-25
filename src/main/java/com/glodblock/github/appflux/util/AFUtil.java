package com.glodblock.github.appflux.util;

import appeng.blockentity.networking.CableBusBlockEntity;
import appeng.blockentity.networking.ControllerBlockEntity;
import appeng.blockentity.networking.EnergyAcceptorBlockEntity;
import appeng.helpers.InterfaceLogicHost;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import com.glodblock.github.appflux.common.tileentities.TileFluxAccessor;
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

    public static boolean isBlackListTE(BlockEntity te) {
        return te instanceof TileFluxAccessor ||
                te instanceof InterfaceLogicHost ||
                te instanceof PatternProviderLogicHost ||
                te instanceof CableBusBlockEntity ||
                te instanceof EnergyAcceptorBlockEntity ||
                te instanceof ControllerBlockEntity;
    }

}
