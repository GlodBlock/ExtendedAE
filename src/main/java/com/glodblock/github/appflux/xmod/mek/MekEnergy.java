package com.glodblock.github.appflux.xmod.mek;

import appeng.api.config.Actionable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

public interface MekEnergy {

    Factory INDUCTION = MekInductionPortHandler::new;
    Factory NULL = NullEnergy::new;

    boolean valid();

    long getStored();

    long input(long power, Actionable mode, Direction side);

    long output(long power, Actionable mode, Direction side);

    interface Factory {
        MekEnergy create(ServerLevel level, BlockPos pos);

    }

}
