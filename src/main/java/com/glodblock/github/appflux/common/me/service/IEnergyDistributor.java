package com.glodblock.github.appflux.common.me.service;

import appeng.api.networking.IGridNodeService;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface IEnergyDistributor extends IGridNodeService {

    default void distribute() {

    }

    default void charge() {

    }

    default void setServiceHost(@Nullable EnergyDistributeService service) {

    }

    /** Whether to run this distributor for the given level tick. */
    default boolean isInLevel(Level level) {
        return true;
    }

    default boolean isActive() {
        return true;
    }

}
