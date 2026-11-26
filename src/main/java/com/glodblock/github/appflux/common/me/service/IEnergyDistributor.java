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

    /** Whether this distributor binds to a fixed {@link Level} via {@link #getDistributorLevel()}. */
    default boolean hasFixedLevel() {
        return false;
    }

    /** Returns this distributor's owning level when known, or {@code null} if not yet attached or dynamic. */
    @Nullable
    default Level getDistributorLevel() {
        return null;
    }

    /** Whether to run this distributor for the given level tick. */
    default boolean isInLevel(Level level) {
        return true;
    }

    default boolean isActive() {
        return true;
    }

}
