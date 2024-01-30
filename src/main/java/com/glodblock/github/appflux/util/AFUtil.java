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
