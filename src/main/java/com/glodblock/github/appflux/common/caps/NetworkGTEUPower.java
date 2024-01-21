package com.glodblock.github.appflux.common.caps;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageService;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import com.glodblock.github.appflux.common.me.key.type.EnergyType;
import com.glodblock.github.appflux.util.AFUtil;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.forge.GTCapability;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

public record NetworkGTEUPower(IStorageService storage, IActionSource source, BlockEntity asker, Direction side) implements IEnergyContainer {

    @Override
    public long acceptEnergyFromNetwork(Direction side, long voltage, long amperage) {
        var toAdd = Math.min(amperage * voltage, this.getEnergyCanBeInserted());
        if (toAdd > 0) {
            var usedAmp = toAdd / voltage;
            toAdd = usedAmp * voltage;
            this.changeEnergy(toAdd);
            return usedAmp;
        }
        return 0;
    }

    @Override
    public boolean inputsEnergy(Direction direction) {
        return true;
    }

    @Override
    public boolean outputsEnergy(Direction side) {
        return true;
    }

    @Override
    public long changeEnergy(long energyToAdd) {
        if (energyToAdd > 0) {
            return this.storage.getInventory().insert(FluxKey.of(EnergyType.GTEU), energyToAdd, Actionable.MODULATE, this.source);
        }
        if (energyToAdd < 0) {
            return -this.storage.getInventory().extract(FluxKey.of(EnergyType.GTEU), -energyToAdd, Actionable.MODULATE, this.source);
        }
        return 0;
    }

    @Override
    public long getEnergyStored() {
        return this.storage.getCachedInventory().get(FluxKey.of(EnergyType.GTEU));
    }

    @Override
    public long getEnergyCapacity() {
        var space = this.storage.getInventory().insert(FluxKey.of(EnergyType.GTEU), Long.MAX_VALUE - 1, Actionable.SIMULATE, this.source);
        return space + this.getEnergyStored();
    }

    @Override
    public long getInputAmperage() {
        return Long.MAX_VALUE;
    }

    @Override
    public long getInputVoltage() {
        return Long.MAX_VALUE;
    }

    @Override
    public long getOutputAmperage() {
        var e = AFUtil.findCapability(this.asker, this.side, GTCapability.CAPABILITY_ENERGY_CONTAINER);
        if (e != null) {
            return e.getInputAmperage();
        }
        return 0L;
    }

    @Override
    public long getOutputVoltage() {
        var e = AFUtil.findCapability(this.asker, this.side, GTCapability.CAPABILITY_ENERGY_CONTAINER);
        if (e != null) {
            return e.getInputVoltage();
        }
        return 0L;
    }

    @Override
    public boolean isOneProbeHidden() {
        return true;
    }
}