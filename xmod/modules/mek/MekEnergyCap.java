package com.glodblock.github.appflux.xmod.mek;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageService;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import com.glodblock.github.appflux.common.me.key.type.EnergyType;
import com.glodblock.github.appflux.config.AFConfig;
import mekanism.api.Action;
import mekanism.api.annotations.NothingNullByDefault;
import mekanism.api.energy.IStrictEnergyHandler;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.integration.energy.forgeenergy.ForgeStrictEnergyHandler;
import mekanism.common.util.UnitDisplayUtils;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.energy.EnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@NothingNullByDefault
public class MekEnergyCap implements IStrictEnergyHandler {

    private final IStorageService storage;
    private final IActionSource source;
    public static final BlockCapability<IStrictEnergyHandler, Direction> CAP = Capabilities.STRICT_ENERGY.block();

    public static IStrictEnergyHandler of(@Nullable IStorageService storage, IActionSource source) {
        if (storage == null) {
            return new ForgeStrictEnergyHandler(new EnergyStorage(0));
        } else {
            return new MekEnergyCap(storage, source);
        }
    }

    public static long send(IStrictEnergyHandler handler, IStorageService storage, IActionSource source) {
        var ioJ = UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertFrom(AFConfig.getFluxAccessorIO());
        var notAddedJ = handler.insertEnergy(ioJ, Action.SIMULATE);
        var toAddJ = ioJ - notAddedJ;
        var toAddFE = UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertTo(toAddJ);
        if (toAddFE > 0) {
            var drainedFE = storage.getInventory().extract(FluxKey.of(EnergyType.FE), toAddFE, Actionable.MODULATE, source);
            if (drainedFE > 0) {
                var drainedJ = UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertFrom(drainedFE);
                var leftJ = handler.insertEnergy(drainedJ, Action.EXECUTE);
                var leftFE = UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertTo(leftJ);
                if (leftFE > 0) {
                    storage.getInventory().insert(FluxKey.of(EnergyType.FE), leftFE, Actionable.MODULATE, source);
                    return UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertTo(drainedJ - leftJ);
                }
                return UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertTo(drainedJ);
            }
        }
        return 0;
    }

    private MekEnergyCap(IStorageService storage, IActionSource source) {
        this.storage = storage;
        this.source = source;
    }

    @Override
    public int getEnergyContainerCount() {
        return 1;
    }

    @Override
    public long getEnergy(int container) {
        if (container == 0) {
            return UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertFrom(this.storage.getCachedInventory().get(FluxKey.of(EnergyType.FE)));
        }
        return 0;
    }

    @Override
    public void setEnergy(int container, long energy) {
        // NO-OP
    }

    @Override
    public long getMaxEnergy(int container) {
        if (container == 0) {
            var space = this.storage.getInventory().insert(FluxKey.of(EnergyType.FE), Long.MAX_VALUE - 1, Actionable.SIMULATE, this.source);
            return UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertFrom(space + this.storage.getCachedInventory().get(FluxKey.of(EnergyType.FE)));
        }
        return 0;
    }

    @Override
    public long getNeededEnergy(int container) {
        if (container == 0) {
            return UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertFrom(this.storage.getInventory().insert(FluxKey.of(EnergyType.FE), Long.MAX_VALUE - 1, Actionable.SIMULATE, this.source));
        }
        return 0;
    }

    @Override
    public long insertEnergy(int container, long amount, @NotNull Action action) {
        if (container == 0) {
            return this.insertEnergy(amount, action);
        }
        return amount;
    }

    @Override
    public long insertEnergy(long amount, Action action) {
        long toInsert = UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertTo(amount);
        if (toInsert > 0L) {
            long inserted = this.storage.getInventory().insert(FluxKey.of(EnergyType.FE), toInsert, Actionable.ofSimulate(action.simulate()), this.source);
            if (inserted > 0L) {
                return amount - UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertFrom(inserted);
            }
        }
        return amount;
    }

    @Override
    public long extractEnergy(int container, long amount, @NotNull Action action) {
        if (container == 0) {
            return this.extractEnergy(amount, action);
        }
        return 0;
    }

    @Override
    public long extractEnergy(long amount, Action action) {
        long toExtract = UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertTo(amount);
        if (toExtract > 0L) {
            long extracted = this.storage.getInventory().extract(FluxKey.of(EnergyType.FE), toExtract, Actionable.ofSimulate(action.simulate()), this.source);
            return UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertFrom(extracted);
        }
        return 0;
    }

}
