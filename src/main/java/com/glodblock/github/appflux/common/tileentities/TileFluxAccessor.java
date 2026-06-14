package com.glodblock.github.appflux.common.tileentities;

import appeng.api.networking.GridFlags;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNodeListener;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageService;
import appeng.blockentity.grid.AENetworkedBlockEntity;
import appeng.util.SettingsFrom;
import com.glodblock.github.appflux.common.AFSingletons;
import com.glodblock.github.appflux.common.caps.NetworkFEPower;
import com.glodblock.github.appflux.common.me.energy.EnergyCapCache;
import com.glodblock.github.appflux.common.me.energy.UniversalEnergyHandler;
import com.glodblock.github.appflux.common.me.energy.EnergyTickRecord;
import com.glodblock.github.appflux.common.me.service.EnergyDistributeService;
import com.glodblock.github.appflux.common.me.service.IEnergyDistributor;
import com.glodblock.github.appflux.config.AFConfig;
import com.glodblock.github.appflux.util.helpers.Constants;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.ICapabilityInvalidationListener;
import net.neoforged.neoforge.transfer.energy.EmptyEnergyHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Set;

public class TileFluxAccessor extends AENetworkedBlockEntity implements IEnergyDistributor {

    private EnergyCapCache cacheApi;
    private boolean fast = false;
    // mutable
    private final Set<Direction> blocked = EnumSet.noneOf(Direction.class);
    private final Reference2ReferenceMap<Direction, EnergyTickRecord> lastTick = new Reference2ReferenceOpenHashMap<>();
    private final ICapabilityInvalidationListener[] listeners = new ICapabilityInvalidationListener[6];
    private final IActionSource source = IActionSource.ofMachine(this);

    public TileFluxAccessor(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        this.getMainNode().setFlags(GridFlags.REQUIRE_CHANNEL);
        this.getMainNode().setIdlePowerUsage(1.0).addService(IEnergyDistributor.class, this);
        for (var d : Constants.ALL_DIRECTIONS_LIST) {
            this.listeners[d.get3DDataValue()] = () -> {
                if (this.isRemoved()) {
                    return false;
                }
                this.blocked.remove(d);
                return true;
            };
        }
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

    public EnergyHandler getEnergyStorage() {
        if (this.getStorage() != null) {
            return new NetworkFEPower(this.getStorage(), this.source);
        } else {
            return EmptyEnergyHandler.INSTANCE;
        }
    }

    public IStorageService getStorage() {
        if (this.getGridNode() != null) {
            return this.getGridNode().getGrid().getStorageService();
        }
        return null;
    }

    @Override
    public void importSettings(SettingsFrom mode, DataComponentMap input, @Nullable Player player) {
        super.importSettings(mode, input, player);
        if (input.has(AFSingletons.FAST_MODE)) {
            this.fast = input.getOrDefault(AFSingletons.FAST_MODE, false);
        }
    }

    @Override
    public void exportSettings(SettingsFrom mode, DataComponentMap.Builder output, @Nullable Player player) {
        super.exportSettings(mode, output, player);
        if (mode == SettingsFrom.MEMORY_CARD) {
            output.set(AFSingletons.FAST_MODE, this.fast);
        }
    }

    @Override
    public void loadTag(ValueInput input) {
        super.loadTag(input);
        this.fast = input.getBooleanOr("fast", false);
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean("fast", this.fast);
    }

    @Override
    public boolean isActive() {
        return this.getMainNode().isActive();
    }

    @Override
    public void distribute(long ticks) {
        if (this.level == null) {
            return;
        }
        if (this.cacheApi == null) {
            this.initCache();
        }
        var storage = this.getStorage();
        var gird = this.getGrid();
        if (storage != null) {
            for (var d : Constants.ALL_DIRECTIONS_LIST) {
                if (this.blocked.contains(d)) {
                    continue;
                }
                var tickRate = this.lastTick.get(d);
                if (this.isFastMode() || tickRate.needTick(ticks)) {
                    long sent = UniversalEnergyHandler.send(this.cacheApi, d, storage, this.source);
                    if (sent == -1) {
                        this.blocked.add(d);
                    } else {
                        tickRate.sent(sent);
                    }
                }
            }
            if (AFConfig.selfCharge() && gird != null) {
                UniversalEnergyHandler.chargeNetwork(gird.getService(IEnergyService.class), storage, this.source);
            }
        }
    }

    @Override
    public void setServiceHost(@Nullable EnergyDistributeService service) {
        if (service != null) {
            service.wake(this);
            this.blocked.clear();
            if (this.getLevel() instanceof ServerLevel world) {
                for (var d : Constants.ALL_DIRECTIONS_LIST) {
                    var pos = this.getBlockPos().relative(d);
                    world.registerCapabilityListener(pos, this.listeners[d.get3DDataValue()]);
                    this.lastTick.put(d, new EnergyTickRecord());
                }
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

    @Override
    public void onMainNodeStateChanged(IGridNodeListener.State reason) {
        if (this.getMainNode().hasGridBooted()) {
            this.invalidateCapabilities();
        }
    }

}
