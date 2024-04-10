package com.glodblock.github.appflux.common.tileentities;

import appeng.api.config.Actionable;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageService;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.blockentity.grid.AENetworkBlockEntity;
import com.glodblock.github.appflux.common.AFItemAndBlock;
import com.glodblock.github.appflux.common.caps.NetworkFEPower;
import com.glodblock.github.appflux.common.caps.NetworkFEPowerL;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import com.glodblock.github.appflux.common.me.key.type.EnergyType;
import com.glodblock.github.appflux.util.AFUtil;
import com.glodblock.github.glodium.util.GlodUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sonar.fluxnetworks.api.FluxCapabilities;
import appeng.api.config.PowerUnits;
import java.lang.reflect.InvocationTargetException;

public class TileFluxAccessor extends AENetworkBlockEntity implements IGridTickable {
    Boolean IsFnLoad = ModList.get().isLoaded("fluxnetworks");

    public TileFluxAccessor(BlockPos pos, BlockState blockState) {
        super(GlodUtil.getTileType(TileFluxAccessor.class, TileFluxAccessor::new, AFItemAndBlock.FLUX_ACCESSOR), pos, blockState);
        this.getMainNode().setFlags(GridFlags.REQUIRE_CHANNEL);
        this.getMainNode().setIdlePowerUsage(1.0).addService(IGridTickable.class, this);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY ||
                (IsFnLoad && cap == FluxCapabilities.FN_ENERGY_STORAGE)) {
            return LazyOptional.of(() -> {
                if (this.getStorage() != null) {
                    if (ModList.get().isLoaded("fluxnetworks")) {
                        return new NetworkFEPowerL(this.getStorage(), this.getSource());
                    }
                    return new NetworkFEPower(this.getStorage(), this.getSource());
                } else {
                    return new EnergyStorage(0);
                }
            }).cast();
        }
        return super.getCapability(cap, side);
    }

    private BlockEntity getAsker(Direction side) {
        if (this.level != null && side != null) {
            this.level.getBlockEntity(this.worldPosition.offset(side.getNormal()));
        }
        return null;
    }

    private IStorageService getStorage() {
        if (this.getGridNode() != null) {
            return this.getGridNode().getGrid().getStorageService();
        }
        return null;
    }

    private IActionSource getSource() {
        return IActionSource.ofMachine(this);
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        return new TickingRequest(1, 1, false, false);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        var storage = this.getStorage();
        var gird = AFUtil.getGrid(this, null);
        if (storage != null && this.level != null) {
            for (var d : Direction.values()) {
                var te = this.level.getBlockEntity(this.worldPosition.offset(d.getNormal()));
                var thatGrid = AFUtil.getGrid(te, d.getOpposite());
                if (te != null && thatGrid != gird && !AFUtil.isBlackListTE(te, d.getOpposite())) {
                    long canAdd = Long.MAX_VALUE;
                    var toAdd = 0L;
                    if (IsFnLoad) {
                        var accepterL = AFUtil.findCapability(te, d.getOpposite(), FluxCapabilities.FN_ENERGY_STORAGE);
                        if (accepterL != null) {
                            toAdd += accepterL.receiveEnergyL(canAdd, true);
                        } else {
                            var accepter = AFUtil.findCapability(te, d.getOpposite(), ForgeCapabilities.ENERGY);
                            if (accepter != null) {
                                toAdd += accepter.receiveEnergy(AFUtil.clampLong(canAdd), true);
                            }
                        }
                    } else {
                        var accepter = AFUtil.findCapability(te, d.getOpposite(), ForgeCapabilities.ENERGY);
                        if (accepter != null) {
                            toAdd += accepter.receiveEnergy(AFUtil.clampLong(canAdd), true);
                        }
                    }
                    canAdd=storage.getInventory().extract(FluxKey.of(EnergyType.FE), toAdd, Actionable.MODULATE, this.getSource());
                    toAdd=0L;
                    if(IsFnLoad){
                        var accepterL = AFUtil.findCapability(te, d.getOpposite(), FluxCapabilities.FN_ENERGY_STORAGE);
                        if(accepterL!=null){
                            toAdd+=accepterL.receiveEnergyL(canAdd,false);
                        }else{
                            var accepter = AFUtil.findCapability(te, d.getOpposite(), ForgeCapabilities.ENERGY);
                            if(accepter!=null){
                                toAdd+=accepter.receiveEnergy(AFUtil.clampLong(canAdd),false);
                            }
                        }
                    }else{
                        var accepter = AFUtil.findCapability(te, d.getOpposite(), ForgeCapabilities.ENERGY);
                        if(accepter!=null){
                            toAdd+=accepter.receiveEnergy(AFUtil.clampLong(canAdd),false);
                        }
                    }
                    storage.getInventory().insert(FluxKey.of(EnergyType.FE), canAdd-toAdd, Actionable.MODULATE, this.getSource());
                }

            }
            if (gird != null) {
                var to_input = gird.getEnergyService().injectPower(9007199254740990L, Actionable.SIMULATE);
                var can_input = storage.getInventory().extract(FluxKey.of(EnergyType.FE), Math.round(PowerUnits.AE.convertTo(PowerUnits.RF,9007199254740990L-to_input)), Actionable.MODULATE, this.getSource());
                gird.getEnergyService().injectPower(PowerUnits.RF.convertTo(PowerUnits.AE,can_input), Actionable.MODULATE);
            }

        }
        return TickRateModulation.SAME;
    }


}
