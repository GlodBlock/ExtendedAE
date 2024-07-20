package com.glodblock.github.appflux.common.me.energy;

import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageService;
import appeng.parts.AEBasePart;
import appeng.parts.PartAdjacentApi;
import com.glodblock.github.appflux.common.me.service.EnergyDistributeService;
import com.glodblock.github.appflux.common.me.service.IEnergyDistributor;
import com.glodblock.github.appflux.util.AFUtil;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.ICapabilityInvalidationListener;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class EnergyTicker implements IEnergyDistributor {

    private EnergyCapCache cache;
    private EnergyDistributeService service;
    private BlockEntity tile;
    private final Supplier<BlockEntity> lazyInit;
    private final Object host;
    private final BooleanSupplier controller;
    private final IManagedGridNode mainNode;
    private final IActionSource source;
    // immutable
    private Set<Direction> validSides;
    // mutable
    private final Set<Direction> blocked = EnumSet.noneOf(Direction.class);
    private final Reference2ReferenceMap<Direction, EnergyTickRecord> lastTick = new Reference2ReferenceOpenHashMap<>();
    private final ICapabilityInvalidationListener[] listeners = new ICapabilityInvalidationListener[6];

    public EnergyTicker(Supplier<BlockEntity> tileGetter, Object host, BooleanSupplier controller, IManagedGridNode node, IActionSource source) {
        this.lazyInit = tileGetter;
        this.controller = controller;
        this.mainNode = node;
        this.source = source;
        this.host = host;
        for (var d : Direction.values()) {
            this.listeners[d.get3DDataValue()] = () -> {
                if (this.host instanceof AEBasePart part) {
                    if (!PartAdjacentApi.isPartValid(part)) {
                        return false;
                    }
                } else if (this.host instanceof BlockEntity te) {
                    if (te.isRemoved()) {
                        return false;
                    }
                } else {
                    return false;
                }
                this.unblock(d);
                return true;
            };
        }
    }

    @Override
    public boolean isActive() {
        return this.mainNode.isActive();
    }

    @Override
    public void distribute(long ticks) {
        if (this.service == null) {
            return;
        }
        if (this.cache == null) {
            this.initCache();
        }
        // Fast Fail
        if (this.validSides.size() == this.blocked.size()) {
            return;
        }
        var storage = this.getStorage();
        if (storage != null) {
            for (var d : this.validSides) {
                if (this.blocked.contains(d)) {
                    continue;
                }
                var tickRate = this.lastTick.get(d);
                if (tickRate.needTick(ticks)) {
                    long sent = EnergyHandler.send(this.cache, d, storage, this.source);
                    if (sent == -1) {
                        this.blocked.add(d);
                    } else {
                        tickRate.sent(sent);
                    }
                }
            }
        }
    }

    @Override
    public void setServiceHost(@Nullable EnergyDistributeService service) {
        this.service = service;
        this.updateSleep();
        if (service != null) {
            this.tile = this.lazyInit.get();
            this.validSides = AFUtil.getSides(this.host);
            this.blocked.clear();
            if (this.tile.getLevel() instanceof ServerLevel world) {
                for (var d : this.validSides) {
                    var pos = this.tile.getBlockPos().relative(d);
                    world.registerCapabilityListener(pos, this.listeners[d.get3DDataValue()]);
                    this.lastTick.put(d, new EnergyTickRecord());
                }
            }
        }
    }

    public void updateSleep() {
        if (this.service != null) {
            if (this.controller.getAsBoolean()) {
                this.service.wake(this);
            } else {
                this.service.sleep(this);
            }
        }
    }

    public void unblock(Direction dir) {
        if (this.validSides.contains(dir)) {
            this.blocked.remove(dir);
        }
    }

    private IStorageService getStorage() {
        if (this.mainNode.getGrid() != null) {
            return this.mainNode.getGrid().getStorageService();
        }
        return null;
    }

    private void initCache() {
        this.cache = new EnergyCapCache((ServerLevel) this.tile.getLevel(), this.tile.getBlockPos(), this.mainNode::getGrid);
    }

}
