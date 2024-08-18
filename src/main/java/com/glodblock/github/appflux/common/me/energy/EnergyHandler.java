package com.glodblock.github.appflux.common.me.energy;

import appeng.api.config.Actionable;
import appeng.api.config.PowerUnit;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageService;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import com.glodblock.github.appflux.common.me.key.type.EnergyType;
import com.glodblock.github.appflux.config.AFConfig;
import com.glodblock.github.appflux.util.AFUtil;
import com.glodblock.github.appflux.xmod.ModConstants;
import com.glodblock.github.appflux.xmod.mek.MekEnergyCap;
import com.glodblock.github.appflux.xmod.mi.MIEnergyCap;
import com.glodblock.github.glodium.util.GlodUtil;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class EnergyHandler {

    private static final ArrayList<Pair<BlockCapability<?, Direction>, Handler<?>>> HANDLERS = new ArrayList<>();
    private static final Handler<IEnergyStorage> DEFAULT = (accepter, storage, source) -> {
        var toAdd = accepter.receiveEnergy(AFUtil.clampLong(AFConfig.getFluxAccessorIO()), true);
        if (toAdd > 0) {
            var drained = storage.getInventory().extract(FluxKey.of(EnergyType.FE), toAdd, Actionable.MODULATE, source);
            if (drained > 0) {
                var actuallyDrained = accepter.receiveEnergy((int) drained, false);
                var differ = drained - actuallyDrained;
                if (differ > 0) {
                    storage.getInventory().insert(FluxKey.of(EnergyType.FE), differ, Actionable.MODULATE, source);
                }
                return actuallyDrained;
            }
        }
        return 0;
    };

    static {
        if (GlodUtil.checkMod(ModConstants.MEK)) {
            addHandler(MekEnergyCap.CAP, MekEnergyCap::send);
        }
        if (GlodUtil.checkMod(ModConstants.MI) && AFConfig.miSupport()) {
            addHandler(MIEnergyCap.CAP, MIEnergyCap::send);
        }
    }

    public static <T> void addHandler(BlockCapability<T, Direction> cap, Handler<T> handler) {
        HANDLERS.add(Pair.of(cap, handler));
    }

    // -1 means this side has no valid energy accepter
    @SuppressWarnings("unchecked")
    public static <T> long send(@NotNull EnergyCapCache cache, Direction side, @NotNull IStorageService storage, @NotNull IActionSource source) {
        for (var entry : HANDLERS) {
            T cap = cache.getEnergyCap((BlockCapability<T, Direction>) entry.left(), side);
            if (cap != null) {
                return ((Handler<T>) entry.right()).send(cap, storage, source);
            }
        }
        var cap = cache.getEnergyCap(Capabilities.EnergyStorage.BLOCK, side);
        if (cap != null) {
            return DEFAULT.send(cap, storage, source);
        }
        return -1;
    }

    public static void chargeNetwork(@NotNull IEnergyService energy, @NotNull IStorageService storage, @NotNull IActionSource source) {
        var toAdd = Math.floor(Integer.MAX_VALUE - energy.injectPower(Integer.MAX_VALUE, Actionable.SIMULATE));
        var toDrain = storage.getInventory().extract(FluxKey.of(EnergyType.FE), (long) PowerUnit.AE.convertTo(PowerUnit.FE, toAdd), Actionable.MODULATE, source);
        energy.injectPower(toDrain, Actionable.MODULATE);
    }

    public interface Handler<T> {

        long send(@NotNull T cap, @NotNull IStorageService storage, @NotNull IActionSource source);

    }

}
