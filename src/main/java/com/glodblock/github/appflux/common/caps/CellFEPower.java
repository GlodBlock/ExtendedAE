package com.glodblock.github.appflux.common.caps;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import com.glodblock.github.appflux.common.me.cell.FluxCellInventory;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import com.glodblock.github.appflux.common.me.key.type.EnergyType;
import com.glodblock.github.appflux.util.AFUtil;
import net.minecraftforge.energy.IEnergyStorage;

public record CellFEPower(FluxCellInventory inv) implements IEnergyStorage {

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return (int) this.inv.insert(FluxKey.of(EnergyType.FE), maxReceive, Actionable.ofSimulate(simulate), IActionSource.empty());
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return (int) this.inv.extract(FluxKey.of(EnergyType.FE), maxExtract, Actionable.ofSimulate(simulate), IActionSource.empty());
    }

    @Override
    public int getEnergyStored() {
        return AFUtil.clampLong(this.inv.getStoredEnergy());
    }

    @Override
    public int getMaxEnergyStored() {
        return AFUtil.clampLong(this.inv.getMaxEnergy());
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public boolean canReceive() {
        return false;
    }
}
