package com.glodblock.github.appflux.common.me.service;

import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridService;
import appeng.api.networking.IGridServiceProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;

public class EnergyDistributeService implements IGridService, IGridServiceProvider {

    private final Map<IGridNode, IEnergyDistributor> distributors = new IdentityHashMap<>();

    public EnergyDistributeService() {
        // NO-OP
    }

    @Override
    public void onLevelEndTick(Level level) {
        for (var dis : this.distributors.values()) {
            dis.distribute();
        }
    }

    @Override
    public void removeNode(IGridNode gridNode) {
        this.distributors.remove(gridNode);
    }

    @Override
    public void addNode(IGridNode gridNode, @Nullable CompoundTag savedData) {
        var distributor = gridNode.getService(IEnergyDistributor.class);
        if (distributor != null) {
            this.distributors.put(gridNode, distributor);
        }
    }

}
