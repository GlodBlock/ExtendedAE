package com.glodblock.github.appflux.common.me.service;

import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridService;
import appeng.api.networking.IGridServiceProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

public class EnergyDistributeService implements IGridService, IGridServiceProvider {

    private final Map<IGridNode, IEnergyDistributor> distributors = new IdentityHashMap<>();
    // IdentityHashMap is faster
    private final Set<IEnergyDistributor> activeNodes = Collections.newSetFromMap(new IdentityHashMap<>());


    public EnergyDistributeService() {
        // NO-OP
    }

    @Override
    public void onLevelEndTick(Level level) {
        for (var dis : this.activeNodes) {
            if (dis.isActive()) {
                dis.distribute();
            }
        }
    }

    @Override
    public void removeNode(IGridNode gridNode) {
        var node = this.distributors.get(gridNode);
        if (node != null) {
            node.setServiceHost(null);
            this.activeNodes.remove(node);
            this.distributors.remove(gridNode);
        }
    }

    @Override
    public void addNode(IGridNode gridNode, @Nullable CompoundTag savedData) {
        var distributor = gridNode.getService(IEnergyDistributor.class);
        if (distributor != null) {
            this.distributors.put(gridNode, distributor);
            distributor.setServiceHost(this);
        }
    }

    public void wake(IEnergyDistributor node) {
        this.activeNodes.add(node);
    }

    public void sleep(IEnergyDistributor node) {
        this.activeNodes.remove(node);
    }

}
