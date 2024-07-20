package com.glodblock.github.appflux.common.me.service;

import appeng.api.networking.IGridNodeService;
import org.jetbrains.annotations.Nullable;

public interface IEnergyDistributor extends IGridNodeService {

    default void distribute(long ticks) {

    }

    default void setServiceHost(@Nullable EnergyDistributeService service) {

    }

    default boolean isActive() {
        return true;
    }

}
