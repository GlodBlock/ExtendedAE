package com.glodblock.github.appflux.xmod.mek;

import appeng.api.config.Actionable;
import com.glodblock.github.appflux.util.TileCache;
import mekanism.api.Action;
import mekanism.api.math.FloatingLong;
import mekanism.common.config.MekanismConfig;
import mekanism.common.tile.multiblock.TileEntityInductionPort;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

public class MekInductionPortHandler implements MekEnergy {

    private final TileCache cache;

    protected MekInductionPortHandler(ServerLevel level, BlockPos fromPos) {
        this.cache = TileCache.create(level, fromPos);
    }

    private static FloatingLong FE2J() {
        return MekanismConfig.general.forgeConversionRate.get();
    }

    @Override
    public boolean valid() {
        return this.cache.find() instanceof TileEntityInductionPort;
    }

    @Override
    public long getStored() {
        if (this.valid()) {
            TileEntityInductionPort port = (TileEntityInductionPort) this.cache.find();
            assert port != null;
            return port.getMultiblock().getEnergy().divide(FE2J()).longValue();
        }
        return 0;
    }

    @Override
    public long input(long power, Actionable mode, Direction side) {
        if (this.valid()) {
            TileEntityInductionPort port = (TileEntityInductionPort) this.cache.find();
            assert port != null;
            var add = FloatingLong.create(power).multiply(FE2J());
            var left = port.getMultiblock().insertEnergy(add, side, Action.fromFluidAction(mode.getFluidAction()));
            return FloatingLong.create(power).subtract(left.divide(FE2J())).longValue();
        }
        return 0;
    }

    @Override
    public long output(long power, Actionable mode, Direction side) {
        if (this.valid()) {
            TileEntityInductionPort port = (TileEntityInductionPort) this.cache.find();
            assert port != null;
            var drain = FloatingLong.create(power).multiply(FE2J());
            var left = port.getMultiblock().extractEnergy(drain, side, Action.fromFluidAction(mode.getFluidAction()));
            return left.divide(FE2J()).longValue();
        }
        return 0;
    }

}