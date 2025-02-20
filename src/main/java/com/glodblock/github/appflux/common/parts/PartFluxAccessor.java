package com.glodblock.github.appflux.common.parts;

import appeng.api.networking.GridFlags;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNodeListener;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageService;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.api.util.AECableType;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import appeng.parts.AEBasePart;
import appeng.parts.PartAdjacentApi;
import appeng.parts.PartModel;
import appeng.util.SettingsFrom;
import com.glodblock.github.appflux.AppFlux;
import com.glodblock.github.appflux.common.AFSingletons;
import com.glodblock.github.appflux.common.caps.NetworkFEPower;
import com.glodblock.github.appflux.common.me.energy.EnergyCapCache;
import com.glodblock.github.appflux.common.me.energy.EnergyHandler;
import com.glodblock.github.appflux.common.me.energy.EnergyTickRecord;
import com.glodblock.github.appflux.common.me.service.EnergyDistributeService;
import com.glodblock.github.appflux.common.me.service.IEnergyDistributor;
import com.glodblock.github.appflux.config.AFConfig;
import com.glodblock.github.appflux.container.ContainerFluxAccessor;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.ICapabilityInvalidationListener;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public class PartFluxAccessor extends AEBasePart implements IEnergyDistributor {

    public static final ResourceLocation RL = AppFlux.id("part/flux_accessor");
    public static final IPartModel MODEL = new PartModel(RL);
    private EnergyCapCache cacheApi;
    private boolean blocked = false;
    private boolean fast = false;
    private EnergyTickRecord lastTick = new EnergyTickRecord();
    private final ICapabilityInvalidationListener listener;
    private final IActionSource source = IActionSource.ofMachine(this);

    public PartFluxAccessor(IPartItem<?> partItem) {
        super(partItem);
        this.getMainNode().setFlags(GridFlags.REQUIRE_CHANNEL);
        this.getMainNode().setIdlePowerUsage(1.0).addService(IEnergyDistributor.class, this);
        this.listener = () -> {
            if (!PartAdjacentApi.isPartValid(this)) {
                return false;
            }
            this.blocked = false;
            return true;
        };
    }

    @Override
    public boolean isActive() {
        return this.getMainNode().isActive();
    }

    public IActionSource getSource() {
        return this.source;
    }

    @Override
    public void onMainNodeStateChanged(IGridNodeListener.State reason) {
        super.onMainNodeStateChanged(reason);
        if (this.getMainNode().hasGridBooted()) {
            this.getBlockEntity().invalidateCapabilities();
        }
    }

    private void initCache() {
        this.cacheApi = new EnergyCapCache((ServerLevel) this.getLevel(), this.getBlockEntity().getBlockPos(), this::getGrid);
    }

    private IGrid getGrid() {
        if (this.getGridNode() == null) {
            return null;
        }
        return this.getGridNode().getGrid();
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

    public IStorageService getStorage() {
        if (this.getGridNode() != null) {
            return this.getGridNode().getGrid().getStorageService();
        }
        return null;
    }

    public IEnergyStorage getEnergyStorage() {
        if (this.getStorage() != null) {
            return new NetworkFEPower(this.getStorage(), this.source);
        } else {
            return new EnergyStorage(0);
        }
    }

    @Override
    public boolean onUseWithoutItem(Player player, Vec3 pos) {
        if (!isClientSide()) {
            MenuOpener.open(ContainerFluxAccessor.TYPE, player, MenuLocators.forPart(this));
        }
        return true;
    }

    @Override
    public void importSettings(SettingsFrom mode, DataComponentMap input, @Nullable Player player) {
        super.importSettings(mode, input, player);
        if (input.has(AFSingletons.FAST_MODE)) {
            this.fast = input.getOrDefault(AFSingletons.FAST_MODE, false);
        }
    }

    @Override
    public void exportSettings(SettingsFrom mode, DataComponentMap.Builder output) {
        super.exportSettings(mode, output);
        if (mode == SettingsFrom.MEMORY_CARD) {
            output.set(AFSingletons.FAST_MODE, this.fast);
        }
    }

    @Override
    public void readFromNBT(CompoundTag extra, HolderLookup.Provider registries) {
        super.readFromNBT(extra, registries);
        this.fast = extra.getBoolean("fast");
    }

    @Override
    public void writeToNBT(CompoundTag extra, HolderLookup.Provider registries) {
        super.writeToNBT(extra, registries);
        extra.putBoolean("fast", this.fast);
    }

    @Override
    public void distribute(long ticks) {
        if (this.getLevel() == null) {
            return;
        }
        if (this.cacheApi == null) {
            this.initCache();
        }
        var storage = this.getStorage();
        var d = this.getSide();
        var gird = this.getGrid();
        if (storage != null && d != null) {
            if (!this.blocked) {
                if (this.isFastMode() || this.lastTick.needTick(ticks)) {
                    long sent = EnergyHandler.send(this.cacheApi, d, storage, this.source);
                    if (sent == -1) {
                        this.blocked = true;
                    } else {
                        this.lastTick.sent(sent);
                    }
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
            this.blocked = false;
            if (this.getLevel() instanceof ServerLevel world) {
                var pos = this.getBlockEntity().getBlockPos().relative(this.getSide());
                world.registerCapabilityListener(pos, this.listener);
                this.lastTick = new EnergyTickRecord();
            }
        }
    }

    @Override
    public boolean isFastMode() {
        return this.fast;
    }

    @Override
    public void setFastMode(boolean mode) {
        this.fast = mode;
    }

}
