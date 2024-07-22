package com.glodblock.github.appflux.xmod.mek;

import appeng.api.config.Actionable;
import com.glodblock.github.appflux.util.TileCache;
import mekanism.api.Action;
import mekanism.common.tile.multiblock.TileEntityInductionPort;
import mekanism.common.util.UnitDisplayUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

public class MekInductionPortHandler implements MekEnergy {

    private final TileCache cache;

    protected MekInductionPortHandler(ServerLevel level, BlockPos fromPos) {
        this.cache = TileCache.create(level, fromPos);
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
            return UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertTo(port.getMultiblock().getEnergy());
        }
        return 0;
    }

    @Override
    public long input(long power, Actionable mode, Direction side) {
        if (this.valid()) {
            TileEntityInductionPort port = (TileEntityInductionPort) this.cache.find();
            assert port != null;
            var add = UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertFrom(power);
            var left = port.getMultiblock().insertEnergy(add, side, Action.fromFluidAction(mode.getFluidAction()));
            return power - UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertTo(left);
        }
        return 0;
    }

    @Override
    public long output(long power, Actionable mode, Direction side) {
        if (this.valid()) {
            TileEntityInductionPort port = (TileEntityInductionPort) this.cache.find();
            assert port != null;
            var drain = UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertFrom(power);
            var left = port.getMultiblock().extractEnergy(drain, side, Action.fromFluidAction(mode.getFluidAction()));
            return UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertTo(left);
        }
        return 0;
    }

}