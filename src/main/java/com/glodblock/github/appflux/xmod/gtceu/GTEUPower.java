package com.glodblock.github.appflux.xmod.gtceu;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageService;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import com.glodblock.github.appflux.common.me.key.type.EnergyType;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.compat.FeCompat;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

// Maximum input is limited to 2A@MAX
public record GTEUPower(IStorageService storage, IActionSource source, Supplier<CompoundTag> data) implements IEnergyContainer {

    private static final String TAG_NAME = "gt_buff";

    public static IEnergyContainer of(@Nullable IStorageService storage, IActionSource source, Supplier<CompoundTag> data) {
        if (storage != null) {
            return new GTEUPower(storage, source, data);
        } else {
            return IEnergyContainer.DEFAULT;
        }
    }

    @Override
    public long acceptEnergyFromNetwork(Direction side, long voltage, long amperage) {
        long buffed = this.data.get().getLong(TAG_NAME);
        // try to clear buffed energy first
        if (buffed != 0) {
            long added = this.storage.getInventory().insert(FluxKey.of(EnergyType.FE), buffed, Actionable.MODULATE, this.source);
            if (added < buffed) {
                this.data.get().putLong(TAG_NAME, buffed - added);
                return 0;
            } else {
                this.data.get().remove(TAG_NAME);
            }
        }
        long packetSize = FeCompat.toFeLong(voltage, FeCompat.ratio(false));
        long toAdd = packetSize * amperage;
        long canAdd = this.storage.getInventory().insert(FluxKey.of(EnergyType.FE), toAdd, Actionable.SIMULATE, this.source);
        if (canAdd > 0) {
            long ampUsed = canAdd / packetSize;
            if (ampUsed * packetSize < canAdd) {
                ampUsed ++;
            }
            toAdd = ampUsed * packetSize;
            long added = this.storage.getInventory().insert(FluxKey.of(EnergyType.FE), toAdd, Actionable.MODULATE, this.source);
            buffed = toAdd - added;
            if (buffed != 0) {
                this.data.get().putLong(TAG_NAME, buffed);
            }
            return ampUsed;
        }
        return 0;
    }

    @Override
    public boolean inputsEnergy(Direction side) {
        return true;
    }

    @Override
    public long changeEnergy(long delta) {
        delta = FeCompat.toFeLong(delta, FeCompat.ratio(false));
        if (delta == 0) {
            return 0;
        } else if (delta > 0) {
            return this.storage.getInventory().insert(FluxKey.of(EnergyType.FE), delta, Actionable.MODULATE, this.source);
        } else {
            return this.storage.getInventory().extract(FluxKey.of(EnergyType.FE), -delta, Actionable.MODULATE, this.source);
        }
    }

    @Override
    public long getEnergyStored() {
        return FeCompat.toEu(this.storage.getCachedInventory().get(FluxKey.of(EnergyType.FE)), FeCompat.ratio(false));
    }

    @Override
    public long getEnergyCapacity() {
        var space = this.storage.getInventory().insert(FluxKey.of(EnergyType.FE), Long.MAX_VALUE - 1, Actionable.SIMULATE, this.source);
        return FeCompat.toEu(space + this.getEnergyStored(), FeCompat.ratio(false));
    }

    @Override
    public long getInputAmperage() {
        return 2;
    }

    @Override
    public long getInputVoltage() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isOneProbeHidden() {
        return true;
    }

}
