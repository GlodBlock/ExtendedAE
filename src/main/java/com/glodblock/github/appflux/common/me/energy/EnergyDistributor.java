package com.glodblock.github.appflux.common.me.energy;

import appeng.api.config.Actionable;
import appeng.api.config.PowerUnits;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageService;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import com.glodblock.github.appflux.common.me.key.type.EnergyType;
import org.jetbrains.annotations.NotNull;

public class EnergyDistributor {

    public static void chargeNetwork(@NotNull IEnergyService energy, @NotNull IStorageService storage, @NotNull IActionSource source) {
        var toAdd = Math.floor(Integer.MAX_VALUE - energy.injectPower(Integer.MAX_VALUE, Actionable.SIMULATE));
        var toDrain = storage.getInventory().extract(FluxKey.of(EnergyType.FE), (long) PowerUnits.AE.convertTo(PowerUnits.FE, toAdd), Actionable.MODULATE, source);
        energy.injectPower(toDrain, Actionable.MODULATE);
    }

}
