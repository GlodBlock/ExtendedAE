package com.glodblock.github.appflux.common.tileentities;

import appeng.api.networking.GridFlags;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNodeListener;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageService;
import appeng.blockentity.grid.AENetworkedBlockEntity;
import com.glodblock.github.appflux.common.AFSingletons;
import com.glodblock.github.appflux.common.caps.NetworkFEPower;
import com.glodblock.github.appflux.common.me.energy.EnergyCapCache;
import com.glodblock.github.appflux.common.me.energy.EnergyHandler;
import com.glodblock.github.appflux.common.me.service.EnergyDistributeService;
import com.glodblock.github.appflux.common.me.service.IEnergyDistributor;
import com.glodblock.github.appflux.config.AFConfig;
import com.glodblock.github.appflux.util.helpers.Constants;
import com.glodblock.github.glodium.util.GlodUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.ICapabilityInvalidationListener;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Set;

public class TileFluxAccessor extends AENetworkedBlockEntity implements IEnergyDistributor {

    private EnergyCapCache cacheApi;
    // mutable
    private final Set<Direction> blocked = EnumSet.noneOf(Direction.class);
    private final ICapabilityInvalidationListener[] listeners = new ICapabilityInvalidationListener[6];
    private final IActionSource source = IActionSource.ofMachine(this);

    public TileFluxAccessor(BlockPos pos, BlockState blockState) {
        super(GlodUtil.getTileType(TileFluxAccessor.class, TileFluxAccessor::new, AFSingletons.FLUX_ACCESSOR), pos, blockState);
        this.getMainNode().setFlags(GridFlags.REQUIRE_CHANNEL);
        this.getMainNode().setIdlePowerUsage(1.0).addService(IEnergyDistributor.class, this);
        for (var d : Constants.ALL_DIRECTIONS) {
            this.listeners[d.get3DDataValue()] = () -> {
                if (this.isRemoved()) {
                    return false;
                }
                this.blocked.remove(d);
                return true;
            };
        }
    }

    public IActionSource getSource() {
        return this.source;
    }

    private void initCache() {
        this.cacheApi = new EnergyCapCache((ServerLevel) this.level, this.worldPosition, this::getGrid);
    }

    private IGrid getGrid() {
        if (this.getGridNode() == null) {
            return null;
        }
        return this.getGridNode().getGrid();
    }

    public IEnergyStorage getEnergyStorage() {
        if (this.getStorage() != null) {
            return new NetworkFEPower(this.getStorage(), this.source);
        } else {
            return new EnergyStorage(0);
        }
    }

    public IStorageService getStorage() {
        if (this.getGridNode() != null) {
            return this.getGridNode().getGrid().getStorageService();
        }
        return null;
    }

    @Override
    public boolean isActive() {
        return this.getMainNode().isActive();
    }

    @Override
    public void distribute() {
        if (this.level == null) {
            return;
        }
        if (this.cacheApi == null) {
            this.initCache();
        }
        var storage = this.getStorage();
        var gird = this.getGrid();
        if (storage != null) {
            for (var d : Constants.ALL_DIRECTIONS) {
                if (this.blocked.contains(d)) {
                    continue;
                }
                if (EnergyHandler.failSend(this.cacheApi, d, storage, this.source)) {
                    this.blocked.add(d);
                }
            }
            if (AFConfig.selfCharge() && gird != null) {
                EnergyHandler.chargeNetwork(gird.getService(IEnergyService.class), storage, this.source);
            }
        }
    }

    @Override
    public void setServiceHost(@Nullable EnergyDistributeService service) {
        if (service != null) {
            service.wake(this);
            this.blocked.clear();
            if (this.getLevel() instanceof ServerLevel world) {
                for (var d : Constants.ALL_DIRECTIONS) {
                    var pos = this.getBlockPos().relative(d);
                    world.registerCapabilityListener(pos, this.listeners[d.get3DDataValue()]);
                }
            }
        }
    }

    @Override
    public void onMainNodeStateChanged(IGridNodeListener.State reason) {
        if (this.getMainNode().hasGridBooted()) {
            this.invalidateCapabilities();
        }
    }

}
