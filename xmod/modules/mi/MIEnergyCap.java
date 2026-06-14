package com.glodblock.github.appflux.xmod.mi;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageService;
import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.energy.EnergyApi;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import aztech.modern_industrialization.config.MIServerConfig;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import com.glodblock.github.appflux.common.me.key.type.EnergyType;
import com.glodblock.github.appflux.config.AFConfig;
import dev.technici4n.grandpower.api.ILongEnergyStorage;
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

    public static long send(ILongEnergyStorage acceptor, IStorageService storage, IActionSource source) {
        long ratio = ratio();
        var ioEU = AFConfig.getFluxAccessorIO() / ratio;
        var toAddEu = acceptor.receive(ioEU, true);
        var toAddFE = toAddEu * ratio;
        if (toAddFE > 0) {
            var drainedFE = storage.getInventory().extract(FluxKey.of(EnergyType.FE), toAddFE, Actionable.MODULATE, source);
            if (drainedFE > 0) {
                var drainedEU = drainedFE / ratio;
                var addedEU = acceptor.receive(drainedEU, false);
                var leftFE = (drainedEU - addedEU) * ratio;
                if (leftFE > 0) {
                    storage.getInventory().insert(FluxKey.of(EnergyType.FE), leftFE, Actionable.MODULATE, source);
                }
                return drainedEU * ratio;
            }
        }
        return 0;
    }

    @Override
    public long receive(long maxReceive, boolean simulate) {
        int ratio = ratio();
        maxReceive *= ratio;
        if (ratio > 1L) {
            maxReceive = super.receive(maxReceive, true) / ratio * ratio;
        }
        return super.receive(maxReceive, simulate) / ratio;
    }

    @Override
    public long extract(long maxExtract, boolean simulate) {
        int ratio = ratio();
        maxExtract *= ratio;
        if (ratio > 1L) {
            maxExtract = super.extract(maxExtract, true) / ratio * ratio;
        }
        return super.extract(maxExtract, simulate) / ratio;
    }

    @Override
    public long getAmount() {
        return super.getAmount() / ratio();
    }

    @Override
    public long getCapacity() {
        return super.getCapacity() / ratio();
    }

    @Override
    public boolean canConnect(CableTier cableTier) {
        return true;
    }

    private static int ratio() {
        return MIServerConfig.INSTANCE.forgeEnergyPerEu.getAsInt();
    }

}
