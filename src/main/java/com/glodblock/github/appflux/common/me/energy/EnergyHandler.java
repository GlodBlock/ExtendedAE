package com.glodblock.github.appflux.common.me.energy;

import appeng.api.config.Actionable;
import appeng.api.config.PowerUnits;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageService;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import com.glodblock.github.appflux.common.me.key.type.EnergyType;
import com.glodblock.github.appflux.config.AFConfig;
import com.glodblock.github.appflux.util.AFUtil;
import com.gregtechceu.gtceu.api.capability.compat.EUToFEProvider;
import com.gregtechceu.gtceu.api.capability.compat.FeCompat;
import com.gregtechceu.gtceu.api.capability.forge.GTCapability;
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
import java.util.function.Predicate;

public final class EnergyHandler {

    private static final ArrayList<Pair<Capability<?>, Handler<?>>> HANDLERS = new ArrayList<>();
    // Blacklist certain handler types.
    private static final ArrayList<Predicate<Object>> FILTER = new ArrayList<>();
    private static final Handler<IEnergyStorage> DEFAULT = (accepter, side, storage, source) -> {
        var toAdd = accepter.receiveEnergy(AFUtil.clampLong(AFConfig.getFluxAccessorIO()), true);
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
            addHandler(FluxCapabilities.FN_ENERGY_STORAGE, (accepter, side, storage, source) -> {
                var toAdd = accepter.receiveEnergyL(AFConfig.getFluxAccessorIO(), true);
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
        if (ModList.get().isLoaded("gtceu") && AFConfig.gteuSupport()) {
            FILTER.add(o -> o instanceof EUToFEProvider.GTEnergyWrapper);
            addHandler(GTCapability.CAPABILITY_ENERGY_CONTAINER, (accepter, side, storage, source) -> {
                var voltage = accepter.getInputVoltage();
                if (voltage <= 0) {
                    return;
                }
                var amperage = accepter.getInputAmperage();
                var toAddEU = Math.min(accepter.getEnergyCanBeInserted(), amperage * voltage);
                var toAdd = Math.min(FeCompat.toFeLong(toAddEU, FeCompat.ratio(false)), AFConfig.getFluxAccessorIO());
                if (toAdd > 0) {
                    var drained = storage.getInventory().extract(FluxKey.of(EnergyType.FE), toAdd, Actionable.MODULATE, source);
                    if (drained > 0) {
                        var drainedEU = FeCompat.toEu(drained, FeCompat.ratio(true));
                        var amp = 1L;
                        if (drainedEU <= voltage) {
                            voltage = drainedEU;
                        } else {
                            amp = Math.min(drainedEU / voltage, amperage);
                        }
                        var actuallyDrainedEU = voltage * accepter.acceptEnergyFromNetwork(side, voltage, amp);
                        var actuallyDrained = FeCompat.toFeLong(actuallyDrainedEU, FeCompat.ratio(false));
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

    private static boolean passFilter(Object cap) {
        for (var f : FILTER) {
            if (f.test(cap)) {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public static <T> SendAction getHandler(BlockEntity te, Direction side) {
        for (var entry : HANDLERS) {
            T cap = AFUtil.findCapability(te, side, (Capability<T>) entry.getLeft());
            if (cap != null && passFilter(cap)) {
                return (service, source) -> ((Handler<T>) entry.getRight()).send(cap, side, service, source);
            }
        }
        var cap = AFUtil.findCapability(te, side, ForgeCapabilities.ENERGY);
        if (cap != null) {
            return (service, source) -> DEFAULT.send(cap, side, service, source);
        }
        return SendAction.NOOP;
    }

    public static void chargeNetwork(@NotNull IEnergyService energy,  @NotNull IStorageService storage, @NotNull IActionSource source) {
        var toAdd = Math.floor(Integer.MAX_VALUE - energy.injectPower(Integer.MAX_VALUE, Actionable.SIMULATE));
        var toDrain = storage.getInventory().extract(FluxKey.of(EnergyType.FE), (long) PowerUnits.AE.convertTo(PowerUnits.FE, toAdd), Actionable.MODULATE, source);
        energy.injectPower(toDrain, Actionable.MODULATE);
    }

    public interface Handler<T> {

        void send(@NotNull T cap, Direction side, @NotNull IStorageService storage, @NotNull IActionSource source);

    }

    public interface SendAction {

        SendAction NOOP = (a, b) -> {};

        void send(@NotNull IStorageService storage, @NotNull IActionSource source);

    }

}
