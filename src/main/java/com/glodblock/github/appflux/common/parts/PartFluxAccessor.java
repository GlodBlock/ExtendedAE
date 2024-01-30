package com.glodblock.github.appflux.common.parts;

import appeng.api.config.Actionable;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageService;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.api.util.AECableType;
import appeng.parts.AEBasePart;
import appeng.parts.PartModel;
import com.glodblock.github.appflux.AppFlux;
import com.glodblock.github.appflux.common.caps.NetworkFEPower;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import com.glodblock.github.appflux.common.me.key.type.EnergyType;
import com.glodblock.github.appflux.util.AFUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;

public class PartFluxAccessor extends AEBasePart implements IGridTickable {

    public static final ResourceLocation RL = AppFlux.id("part/flux_accessor");
    public static final IPartModel MODEL = new PartModel(RL);

    public PartFluxAccessor(IPartItem<?> partItem) {
        super(partItem);
        this.getMainNode().setFlags(GridFlags.REQUIRE_CHANNEL);
        this.getMainNode().setIdlePowerUsage(1.0).addService(IGridTickable.class, this);
    }

    @Override
    public void getBoxes(IPartCollisionHelper bch) {
        bch.addBox(2.0, 2.0, 14.0, 14.0, 14.0, 16.0);
        bch.addBox(4.0, 4.0, 12.0, 12.0, 12.0, 14.0);
    }

    @Override
    public float getCableConnectionLength(AECableType cable) {
        return 2.0F;
    }

    @Override
    public IPartModel getStaticModels() {
        return MODEL;
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
    public <T> LazyOptional<T> getCapability(Capability<T> cap) {
        if (cap == ForgeCapabilities.ENERGY && this.getStorage() != null) {
            return LazyOptional.of(() -> new NetworkFEPower(this.getStorage(), this.getSource())).cast();
        }
        return super.getCapability(cap);
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode iGridNode) {
        return new TickingRequest(1, 1, false, false);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode iGridNode, int i) {
        var storage = this.getStorage();
        var d = this.getSide();
        if (storage != null && d != null && this.getLevel() != null) {
            var te = this.getLevel().getBlockEntity(this.getBlockEntity().getBlockPos().offset(d.getNormal()));
            if (te != null && !AFUtil.isBlackListTE(te)) {
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
        return TickRateModulation.SAME;
    }

}
