package com.glodblock.github.appflux.common.me.service;

import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridService;
import appeng.api.networking.IGridServiceProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EnergyDistributeService implements IGridService, IGridServiceProvider {

    private final Map<IGridNode, IEnergyDistributor> distributors = new IdentityHashMap<>();
    // IdentityHashMap is faster
    private final Set<IEnergyDistributor> activeNodes = Collections.newSetFromMap(new IdentityHashMap<>());
    private final Map<Level, List<IEnergyDistributor>> levelBuckets = new IdentityHashMap<>();
    private final List<IEnergyDistributor> unassigned = new ArrayList<>();
    private final List<IEnergyDistributor> fallback = new ArrayList<>();
    private boolean bucketsDirty = true;

    public EnergyDistributeService() {
        // NO-OP
    }

    private void rebuildBuckets() {
        this.levelBuckets.clear();
        this.unassigned.clear();
        this.fallback.clear();
        for (var dis : this.activeNodes) {
            if (!dis.hasFixedLevel()) {
                this.fallback.add(dis);
                continue;
            }
            var nodeLevel = dis.getDistributorLevel();
            if (nodeLevel == null) {
                this.unassigned.add(dis);
            } else {
                this.levelBuckets.computeIfAbsent(nodeLevel, k -> new ArrayList<>()).add(dis);
            }
        }
        this.bucketsDirty = false;
    }

    private void promoteUnassigned() {
        for (int i = this.unassigned.size() - 1; i >= 0; i--) {
            var dis = this.unassigned.get(i);
            var nodeLevel = dis.getDistributorLevel();
            if (nodeLevel != null) {
                this.unassigned.remove(i);
                this.levelBuckets.computeIfAbsent(nodeLevel, k -> new ArrayList<>()).add(dis);
            }
        }
    }

    @Override
    public void onLevelEndTick(Level level) {
        if (level == null) {
            return;
        }
        if (this.activeNodes.isEmpty()) {
            if (!this.levelBuckets.isEmpty() || !this.unassigned.isEmpty() || !this.fallback.isEmpty()) {
                this.levelBuckets.clear();
                this.unassigned.clear();
                this.fallback.clear();
            }
            this.bucketsDirty = false;
            return;
        }
        if (this.bucketsDirty) {
            this.rebuildBuckets();
        } else if (!this.unassigned.isEmpty()) {
            this.promoteUnassigned();
        }

        // AE2 calls this service for every server level, so O(1)-skip dimensions with no distributors.
        var levelNodes = this.levelBuckets.get(level);
        if ((levelNodes == null || levelNodes.isEmpty()) && this.fallback.isEmpty()) {
            return;
        }

        boolean charged = false;
        for (int i = 0, n = this.fallback.size(); i < n; i++) {
            var dis = this.fallback.get(i);
            if (dis.isInLevel(level) && dis.isActive()) {
                dis.distribute();
                if (!charged) {
                    dis.charge();
                    charged = true;
                }
            }
        }

        if (levelNodes == null) {
            return;
        }
        for (int i = 0, n = levelNodes.size(); i < n; i++) {
            var dis = levelNodes.get(i);
            if (dis.isActive()) {
                dis.distribute();
                if (!charged) {
                    dis.charge();
                    charged = true;
                }
            }
        }
    }

    @Override
    public void removeNode(IGridNode gridNode) {
        var node = this.distributors.get(gridNode);
        if (node != null) {
            node.setServiceHost(null);
            if (this.activeNodes.remove(node)) {
                this.bucketsDirty = true;
            }
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
        if (this.activeNodes.add(node)) {
            this.bucketsDirty = true;
        }
    }

    public void sleep(IEnergyDistributor node) {
        if (this.activeNodes.remove(node)) {
            this.bucketsDirty = true;
        }
    }

}
