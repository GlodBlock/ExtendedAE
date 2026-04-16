package com.glodblock.github.extendedae.util;

import com.glodblock.github.extendedae.common.me.wireless.WirelessFail;
import com.glodblock.github.extendedae.common.tileentities.TileWirelessHub;
import com.glodblock.github.extendedae.config.EPPConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class WirelessChecks {
    public static boolean hasNoPorts(TileWirelessHub tile, Player player) {
        if (tile.allocatePort() < 0) {
            player.displayClientMessage(WirelessFail.OUT_OF_PORT.getTranslation(), true);

            return true;
        }

        return false;
    }

    public static boolean hasNoNBT(CompoundTag nbt, Player player) {
        if (!nbt.contains("bind")) {
            player.displayClientMessage(WirelessFail.MISSING.getTranslation(), true);

            return true;
        }

        return false;
    }

    public static boolean hasNoGlobalPos(GlobalPos globalPos, Player player) {
        if (globalPos == null) {
            player.displayClientMessage(WirelessFail.MISSING.getTranslation(), true);

            return true;
        }

        return false;
    }

    public static boolean positionChecks(GlobalPos globalPos, BlockPos pos, Level world, ServerLevel server, Player player) {
        var otherPos = globalPos.pos();
        var otherWorld = globalPos.dimension();
        var thisWorld = world.dimension();

        if (otherPos.equals(pos) && otherWorld.equals(thisWorld)) {
            player.displayClientMessage(WirelessFail.SELF_REFERENCE.getTranslation(), true);

            return true;
        }
        if (!otherWorld.equals(thisWorld)) {
            player.displayClientMessage(WirelessFail.CROSS_DIMENSION.getTranslation(), true);

            return true;
        }
        if (Math.sqrt(otherPos.distSqr(pos)) > EPPConfig.wirelessMaxRange) {
            player.displayClientMessage(WirelessFail.OUT_OF_RANGE.getTranslation(), true);

            return true;
        }
        var otherWorldInstance = server.getServer().getLevel(otherWorld);

        if (otherWorldInstance == null) {
            player.displayClientMessage(WirelessFail.MISSING.getTranslation(), true);

            return true;
        }

        return false;
    }
}
