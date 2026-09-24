package com.glodblock.github.appflux.common.caps;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageService;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import com.glodblock.github.appflux.common.me.key.type.EnergyType;
import com.glodblock.github.appflux.util.AFUtil;
import com.glodblock.github.appflux.util.IOSignal;
import net.neoforged.neoforge.energy.IEnergyStorage;

public record NetworkFEPower(IStorageService storage, IActionSource source, IOSignal signal) implements IEnergyStorage {

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (this.signal.isInput())  {
            return (int) this.storage.getInventory().insert(FluxKey.of(EnergyType.FE), maxReceive, Actionable.ofSimulate(simulate), this.source);
        } else {
            return 0;
        }
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (this.signal.isOutput())  {
            return (int) this.storage.getInventory().extract(FluxKey.of(EnergyType.FE), maxExtract, Actionable.ofSimulate(simulate), this.source);
        } else {
            return 0;
        }
    }

    @Override
    public int getEnergyStored() {
        return AFUtil.clampLong(this.storage.getCachedInventory().get(FluxKey.of(EnergyType.FE)));
    }

    @Override
    public int getMaxEnergyStored() {
        var space = this.storage.getInventory().insert(FluxKey.of(EnergyType.FE), Long.MAX_VALUE - 1, Actionable.SIMULATE, this.source);
        return AFUtil.clampLong(space + this.getEnergyStored());
    }

    @Override
    public boolean canExtract() {
        return this.signal.isOutput();
    }

    @Override
    public boolean canReceive() {
        return this.signal.isInput();
    }

}
