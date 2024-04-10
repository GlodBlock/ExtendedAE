package com.glodblock.github.appflux.common.caps;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageService;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import com.glodblock.github.appflux.common.me.key.type.EnergyType;
import com.glodblock.github.appflux.util.AFUtil;
import net.minecraftforge.energy.IEnergyStorage;
import sonar.fluxnetworks.api.energy.IFNEnergyStorage;


public record NetworkFEPowerL(IStorageService storage, IActionSource source) implements IEnergyStorage,IFNEnergyStorage{

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return (int) this.storage.getInventory().insert(FluxKey.of(EnergyType.FE), maxReceive, Actionable.ofSimulate(simulate), this.source);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return (int) this.storage.getInventory().extract(FluxKey.of(EnergyType.FE), maxExtract, Actionable.ofSimulate(simulate), this.source);
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


    public long receiveEnergyL(long l, boolean b) {
        return this.storage.getInventory().insert(FluxKey.of(EnergyType.FE), l, Actionable.ofSimulate(b), this.source);
    }


    public long extractEnergyL(long l, boolean b) {
        return this.storage.getInventory().extract(FluxKey.of(EnergyType.FE), l, Actionable.ofSimulate(b), this.source);
    }


    public long getEnergyStoredL() {
        return this.storage.getCachedInventory().get(FluxKey.of(EnergyType.FE));
    }

    public long getMaxEnergyStoredL() {
        var space = this.storage.getInventory().insert(FluxKey.of(EnergyType.FE), Long.MAX_VALUE - 1, Actionable.SIMULATE, this.source);
        return space + this.getEnergyStored();
    }


    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public boolean canReceive() {
        return true;
    }


}
