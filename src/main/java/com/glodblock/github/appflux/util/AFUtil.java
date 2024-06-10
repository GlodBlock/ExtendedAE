package com.glodblock.github.appflux.util;

import appeng.api.parts.IPartHost;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.parts.AEBasePart;
import com.glodblock.github.appflux.common.AFItemAndBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.ItemCapability;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Set;

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

    public static boolean shouldTryCast(BlockEntity tile, Direction side) {
        if (tile instanceof IUpgradeableObject upgradeable) {
            return upgradeable.isUpgradedWith(AFItemAndBlock.INDUCTION_CARD);
        }
        if (tile instanceof IPartHost host) {
            if (host.getPart(side) instanceof IUpgradeableObject upgradeable) {
                return upgradeable.isUpgradedWith(AFItemAndBlock.INDUCTION_CARD);
            }
        }
        return true;
    }

    public static Set<Direction> getSides(Object host) {
        if (host instanceof BlockEntity) {
            return EnumSet.allOf(Direction.class);
        } else if (host instanceof AEBasePart part) {
            if (part.getSide() == null) {
                return EnumSet.noneOf(Direction.class);
            }
            return EnumSet.of(part.getSide());
        } else {
            return EnumSet.noneOf(Direction.class);
        }
    }

}
