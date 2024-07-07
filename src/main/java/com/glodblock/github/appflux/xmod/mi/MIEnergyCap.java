package com.glodblock.github.appflux.xmod.mi;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageService;
import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.energy.EnergyApi;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import com.glodblock.github.appflux.common.me.key.type.EnergyType;
import com.glodblock.github.appflux.config.AFConfig;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

public class MIEnergyCap implements MIEnergyStorage {

    private final IStorageService storage;
    private final IActionSource source;
    public static final BlockCapability<MIEnergyStorage, Direction> CAP = EnergyApi.SIDED;

    private MIEnergyCap(IStorageService storage, IActionSource source) {
        this.storage = storage;
        this.source = source;
    }

    public static MIEnergyStorage of(@Nullable IStorageService storage, IActionSource source) {
        if (storage == null) {
            return EnergyApi.EMPTY;
        } else {
            return new MIEnergyCap(storage, source);
        }
    }

    public static void send(MIEnergyStorage accepter, IStorageService storage, IActionSource source) {
        var toAdd = accepter.receive(AFConfig.getFluxAccessorIO(), true);
        if (toAdd > 0) {
            var drained = storage.getInventory().extract(FluxKey.of(EnergyType.FE), toAdd, Actionable.MODULATE, source);
            if (drained > 0) {
                var actuallyDrained = accepter.receive(drained, false);
                var differ = drained - actuallyDrained;
                if (differ > 0) {
                    storage.getInventory().insert(FluxKey.of(EnergyType.FE), differ, Actionable.MODULATE, source);
                }
            }
        }
    }

    @Override
    public boolean canConnect(CableTier cableTier) {
        return true;
    }

    @Override
    public long receive(long maxReceive, boolean simulate) {
        return this.storage.getInventory().insert(FluxKey.of(EnergyType.FE), maxReceive, Actionable.ofSimulate(simulate), this.source);
    }

    @Override
    public long extract(long maxExtract, boolean simulate) {
        return this.storage.getInventory().extract(FluxKey.of(EnergyType.FE), maxExtract, Actionable.ofSimulate(simulate), this.source);
    }

    @Override
    public long getAmount() {
        return this.storage.getCachedInventory().get(FluxKey.of(EnergyType.FE));
    }

    @Override
    public long getCapacity() {
        var space = this.storage.getInventory().insert(FluxKey.of(EnergyType.FE), Long.MAX_VALUE - 1, Actionable.SIMULATE, this.source);
        return space + this.getAmount();
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
