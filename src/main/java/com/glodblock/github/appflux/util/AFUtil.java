package com.glodblock.github.appflux.util;

import appeng.api.networking.IGrid;
import appeng.api.networking.IInWorldGridNodeHost;
import appeng.blockentity.networking.CableBusBlockEntity;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.me.helpers.IGridConnectedBlockEntity;
import com.glodblock.github.appflux.common.parts.PartFluxAccessor;
import com.glodblock.github.appflux.common.tileentities.TileFluxAccessor;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;
import org.jetbrains.annotations.Nullable;

public class AFUtil {

    public static int clampLong(long value) {
        return (int) Math.min(value, Integer.MAX_VALUE);
    }

    @Nullable
    public static <T> T findCapability(ItemStack stack, ItemCapability<T, Void> capability) {
        if (!stack.isEmpty()) {
            return stack.getCapability(capability);
        }
        return null;
    }

    @Nullable
    public static <T, C> T findCapability(BlockEntity tile, BlockCapability<T, C> capability, C context) {
        if (tile != null && tile.getLevel() != null) {
            return tile.getLevel().getCapability(capability, tile.getBlockPos(), tile.getBlockState(), tile, context);
        }
        return null;
    }

    public static boolean isBlackListTE(BlockEntity te, Direction face) {
        if (te instanceof CableBusBlockEntity cable) {
            var part = cable.getPart(face);
            return part instanceof PatternProviderLogicHost ||
                    part instanceof PartFluxAccessor;
        }
        return te instanceof TileFluxAccessor ||
                te instanceof PatternProviderLogicHost;
    }

    public static IGrid getGrid(Object a, Direction side) {
        if (a instanceof IGridConnectedBlockEntity ba) {
            var gn = ba.getGridNode();
            return gn == null ? null : gn.getGrid();
        } else if (a instanceof IInWorldGridNodeHost ha) {
            var gn = ha.getGridNode(side);
            return gn == null ? null : gn.getGrid();
        }
        return null;
    }

}
