package com.glodblock.github.appflux.xmod.mi;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageService;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import com.glodblock.github.appflux.common.me.key.type.EnergyType;
import com.glodblock.github.appflux.config.AFConfig;
import dev.technici4n.grandpower.api.ILongEnergyStorage;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.energy.EmptyEnergyStorage;
import org.jetbrains.annotations.Nullable;

public class LongEnergyCap implements ILongEnergyStorage {

    protected final IStorageService storage;
    protected final IActionSource source;
    protected static final ILongEnergyStorage EMPTY = ILongEnergyStorage.of(EmptyEnergyStorage.INSTANCE);
    public static final BlockCapability<ILongEnergyStorage, Direction> CAP = ILongEnergyStorage.BLOCK;

    protected LongEnergyCap(IStorageService storage, IActionSource source) {
        this.storage = storage;
        this.source = source;
    }

    public static ILongEnergyStorage of(@Nullable IStorageService storage, IActionSource source) {
        if (storage == null) {
            return EMPTY;
        } else {
            return new LongEnergyCap(storage, source);
        }
    }

    public static long send(ILongEnergyStorage acceptor, IStorageService storage, IActionSource source) {
        var toAdd = acceptor.receive(AFConfig.getFluxAccessorIO(), true);
        if (toAdd > 0) {
            var drained = storage.getInventory().extract(FluxKey.of(EnergyType.FE), toAdd, Actionable.MODULATE, source);
            if (drained > 0) {
                var actuallyDrained = acceptor.receive(drained, false);
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
