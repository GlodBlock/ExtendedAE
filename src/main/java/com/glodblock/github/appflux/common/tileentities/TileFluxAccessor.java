package com.glodblock.github.appflux.common.tileentities;

import appeng.api.networking.GridFlags;
import appeng.api.networking.IGrid;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageService;
import appeng.blockentity.grid.AENetworkBlockEntity;
import com.glodblock.github.appflux.common.AFSingletons;
import com.glodblock.github.appflux.common.caps.NetworkFEPower;
import com.glodblock.github.appflux.common.me.energy.EnergyCapCache;
import com.glodblock.github.appflux.common.me.energy.EnergyHandler;
import com.glodblock.github.appflux.common.me.service.IEnergyDistributor;
import com.glodblock.github.appflux.config.AFConfig;
import com.glodblock.github.glodium.util.GlodUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class TileFluxAccessor extends AENetworkBlockEntity implements IEnergyDistributor {

    private EnergyCapCache cacheApi;

    public TileFluxAccessor(BlockPos pos, BlockState blockState) {
        super(GlodUtil.getTileType(TileFluxAccessor.class, TileFluxAccessor::new, AFSingletons.FLUX_ACCESSOR), pos, blockState);
        this.getMainNode().setFlags(GridFlags.REQUIRE_CHANNEL);
        this.getMainNode().setIdlePowerUsage(1.0).addService(IEnergyDistributor.class, this);
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
            return new NetworkFEPower(this.getStorage(), this.getSource());
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

    public IActionSource getSource() {
        return IActionSource.ofMachine(this);
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
            for (var d : Direction.values()) {
                EnergyHandler.send(this.cacheApi, d, storage, this.getSource());
            }
            if (AFConfig.selfCharge() && gird != null) {
                EnergyHandler.chargeNetwork(gird.getService(IEnergyService.class), storage, this.getSource());
            }
        }
    }

}
