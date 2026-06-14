package com.glodblock.github.appflux.xmod.fluxnetwork;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageService;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import com.glodblock.github.appflux.common.me.key.type.EnergyType;
import com.glodblock.github.appflux.config.AFConfig;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;
import sonar.fluxnetworks.api.FluxCapabilities;
import sonar.fluxnetworks.api.energy.FNEnergyStorage;
import sonar.fluxnetworks.api.energy.IFNEnergyStorage;

public record FluxNetworkCap(IStorageService storage, IActionSource source) implements IFNEnergyStorage {

    public static final BlockCapability<IFNEnergyStorage, Direction> CAP = FluxCapabilities.BLOCK;

    public static IFNEnergyStorage of(@Nullable IStorageService storage, IActionSource source) {
        if (storage != null) {
            return new FluxNetworkCap(storage, source);
        } else {
            return new FNEnergyStorage(0);
        }
    }

    public static long send(IFNEnergyStorage accepter, IStorageService storage, IActionSource source) {
        var toAdd = accepter.receiveEnergyL(AFConfig.getFluxAccessorIO(), true);
        if (toAdd > 0) {
            var drained = storage.getInventory().extract(FluxKey.of(EnergyType.FE), toAdd, Actionable.MODULATE, source);
            if (drained > 0) {
                var actuallyDrained = accepter.receiveEnergyL(drained, false);
                var differ = drained - actuallyDrained;
                if (differ > 0) {
                    storage.getInventory().insert(FluxKey.of(EnergyType.FE), differ, Actionable.MODULATE, source);
                }
                return actuallyDrained;
            }
        }
        return 0;
    }

    @Override
    public long receiveEnergyL(long maxReceive, boolean simulate) {
        return this.storage.getInventory().insert(FluxKey.of(EnergyType.FE), maxReceive, Actionable.ofSimulate(simulate), this.source);
    }

    @Override
    public long extractEnergyL(long maxExtract, boolean simulate) {
        return this.storage.getInventory().extract(FluxKey.of(EnergyType.FE), maxExtract, Actionable.ofSimulate(simulate), this.source);
    }

    @Override
    public long getEnergyStoredL() {
        return this.storage.getCachedInventory().get(FluxKey.of(EnergyType.FE));
    }

    @Override
    public long getMaxEnergyStoredL() {
        var space = this.storage.getInventory().insert(FluxKey.of(EnergyType.FE), Long.MAX_VALUE - 1, Actionable.SIMULATE, this.source);
        return space + this.getEnergyStoredL();
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
