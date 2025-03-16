package com.glodblock.github.appflux.xmod.mi;

import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageService;
import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.energy.EnergyApi;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

public class MIEnergyCap extends LongEnergyCap implements MIEnergyStorage {

    public static final BlockCapability<MIEnergyStorage, Direction> CAP = EnergyApi.SIDED;

    protected MIEnergyCap(IStorageService storage, IActionSource source) {
        super(storage, source);
    }

    public static MIEnergyStorage of(@Nullable IStorageService storage, IActionSource source) {
        if (storage == null) {
            return EnergyApi.EMPTY;
        } else {
            return new MIEnergyCap(storage, source);
        }
    }

    @Override
    public boolean canConnect(CableTier cableTier) {
        return true;
    }

}
