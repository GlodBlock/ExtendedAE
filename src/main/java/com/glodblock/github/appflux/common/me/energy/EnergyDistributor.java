package com.glodblock.github.appflux.common.me.energy;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageService;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import com.glodblock.github.appflux.common.me.key.type.EnergyType;
import com.glodblock.github.appflux.util.AFUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.ModList;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import sonar.fluxnetworks.api.FluxCapabilities;

import java.util.ArrayList;

public final class EnergyDistributor {

    private static final ArrayList<Pair<Capability<?>, Handler<?>>> HANDLERS = new ArrayList<>();
    private static final Handler<IEnergyStorage> DEFAULT = (accepter, storage, source) -> {
        var toAdd = accepter.receiveEnergy(Integer.MAX_VALUE, true);
        if (toAdd > 0) {
            var drained = storage.getInventory().extract(FluxKey.of(EnergyType.FE), toAdd, Actionable.MODULATE, source);
            if (drained > 0) {
                var actuallyDrained = accepter.receiveEnergy((int) drained, false);
                var differ = drained - actuallyDrained;
                if (differ > 0) {
                    storage.getInventory().insert(FluxKey.of(EnergyType.FE), differ, Actionable.MODULATE, source);
                }
            }
        }
    };

    static {
        if (ModList.get().isLoaded("fluxnetworks")) {
            addHandler(FluxCapabilities.FN_ENERGY_STORAGE, (accepter, storage, source) -> {
                var toAdd = accepter.receiveEnergyL(Long.MAX_VALUE, true);
                if (toAdd > 0) {
                    var drained = storage.getInventory().extract(FluxKey.of(EnergyType.FE), toAdd, Actionable.MODULATE, source);
                    if (drained > 0) {
                        var actuallyDrained = accepter.receiveEnergyL(drained, false);
                        var differ = drained - actuallyDrained;
                        if (differ > 0) {
                            storage.getInventory().insert(FluxKey.of(EnergyType.FE), differ, Actionable.MODULATE, source);
                        }
                    }
                }
            });
        }
    }

    public static <T> void addHandler(Capability<T> cap, Handler<T> handler) {
        HANDLERS.add(new ImmutablePair<>(cap, handler));
    }

    @SuppressWarnings("unchecked")
    public static <T> void send(BlockEntity te, Direction side, @NotNull IStorageService storage, @NotNull IActionSource source) {
        for (var entry : HANDLERS) {
            T cap = AFUtil.findCapability(te, side, (Capability<T>) entry.getLeft());
            if (cap != null) {
                ((Handler<T>) entry.getRight()).send(cap, storage, source);
                return;
            }
        }
        var cap = AFUtil.findCapability(te, side, ForgeCapabilities.ENERGY);
        if (cap != null) {
            DEFAULT.send(cap, storage, source);
        }
    }

    public interface Handler<T> {

        void send(@NotNull T cap, @NotNull IStorageService storage, @NotNull IActionSource source);

    }

}
