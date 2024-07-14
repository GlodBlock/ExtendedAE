package com.glodblock.github.appflux.common.me.energy;

import appeng.api.AECapabilities;
import appeng.api.networking.IGrid;
import appeng.api.networking.IInWorldGridNodeHost;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class EnergyCapCache {

    private final Map<BlockCapability<?, Direction>, BlockCapabilityCache<?, Direction>[]> cache = new IdentityHashMap<>();
    private final Map<Direction, BlockCapabilityCache<IInWorldGridNodeHost, Void>> girdCache = new IdentityHashMap<>();

    private final ServerLevel world;
    private final BlockPos pos;
    private final Supplier<IGrid> self;

    public EnergyCapCache(ServerLevel world, BlockPos pos, Supplier<IGrid> gridSupplier) {
        this.world = world;
        this.pos = pos;
        this.self = gridSupplier;
    }

    @Nullable
    public <T> T getEnergyCap(BlockCapability<T, Direction> cap, Direction side) {
        if (this.checkGrid(side)) {
            return this.getEnergyCacheInternal(cap, side).getCapability();
        }
        return null;
    }

    private boolean checkGrid(Direction side) {
        var gird = this.getGridCache(side).getCapability();
        var thisGrid = this.self.get();
        if (gird != null && thisGrid != null) {
            var thatGrid = gird.getGridNode(side.getOpposite());
            if (thatGrid == null) {
                return true;
            }
            return thatGrid.getGrid() != thisGrid;
        }
        return true;
    }

    private BlockCapabilityCache<IInWorldGridNodeHost, Void> getGridCache(Direction side) {
        return this.girdCache.computeIfAbsent(side, face -> BlockCapabilityCache.create(AECapabilities.IN_WORLD_GRID_NODE_HOST, this.world, this.pos.relative(face), null));
    }

    @SuppressWarnings("unchecked")
    private <T> BlockCapabilityCache<T, Direction> getEnergyCacheInternal(BlockCapability<T, Direction> cap, Direction side) {
        var capArr = this.cache.computeIfAbsent(cap, __ -> new BlockCapabilityCache[6]);
        int index = side.get3DDataValue();
        if (capArr[index] == null) {
            capArr[index] = BlockCapabilityCache.create(cap, this.world, this.pos.relative(side), side.getOpposite());
        }
        return (BlockCapabilityCache<T, Direction>) capArr[index];
    }

}
