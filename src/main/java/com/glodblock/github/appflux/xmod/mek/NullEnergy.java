package com.glodblock.github.appflux.xmod.mek;

import appeng.api.config.Actionable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

public class NullEnergy implements MekEnergy {

    protected NullEnergy(ServerLevel level, BlockPos fromPos) {
        // NO-OP
    }

    @Override
    public boolean valid() {
        return false;
    }

    @Override
    public long getStored() {
        return 0;
    }

    @Override
    public long input(long power, Actionable mode, Direction side) {
        return 0;
    }

    @Override
    public long output(long power, Actionable mode, Direction side) {
        return 0;
    }

}
