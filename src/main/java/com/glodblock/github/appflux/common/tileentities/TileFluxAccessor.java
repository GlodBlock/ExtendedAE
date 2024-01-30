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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TileFluxAccessor extends AENetworkBlockEntity implements IGridTickable {

    public TileFluxAccessor(BlockPos pos, BlockState blockState) {
        super(GlodUtil.getTileType(TileFluxAccessor.class, TileFluxAccessor::new, AFItemAndBlock.FLUX_ACCESSOR), pos, blockState);
        this.getMainNode().setFlags(GridFlags.REQUIRE_CHANNEL);
        this.getMainNode().setIdlePowerUsage(1.0).addService(IGridTickable.class, this);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        /*if (ModList.get().isLoaded("gtceu")) {
            if (cap == GTCapability.CAPABILITY_ENERGY_CONTAINER && this.getStorage() != null) {
                return LazyOptional.of(() -> new NetworkGTEUPower(this.getStorage(), this.getSource(), this.getAsker(side), side != null ? side.getOpposite() : null)).cast();
            }
        }*/
        if (cap == ForgeCapabilities.ENERGY && this.getStorage() != null) {
            return LazyOptional.of(() -> new NetworkFEPower(this.getStorage(), this.getSource())).cast();
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
                    var accepter = AFUtil.findCapability(te, d.getOpposite(), ForgeCapabilities.ENERGY);
                    if (accepter != null) {
                        var toAdd = accepter.receiveEnergy(Integer.MAX_VALUE, true);
                        if (toAdd > 0) {
                            var drained = storage.getInventory().extract(FluxKey.of(EnergyType.FE), toAdd, Actionable.MODULATE, this.getSource());
                            if (drained > 0) {
                                var actuallyDrained = accepter.receiveEnergy((int) drained, false);
                                var differ = drained - actuallyDrained;
                                if (differ > 0) {
                                    storage.getInventory().insert(FluxKey.of(EnergyType.FE), differ, Actionable.MODULATE, this.getSource());
                                }
                            }
                        }
                    }
                }
            }
        }
        return TickRateModulation.SAME;
    }

}
