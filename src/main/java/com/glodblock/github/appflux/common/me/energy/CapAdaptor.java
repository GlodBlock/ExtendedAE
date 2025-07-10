package com.glodblock.github.appflux.common.me.energy;

import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageService;
import com.glodblock.github.appflux.common.caps.NetworkFEPower;
import com.glodblock.github.appflux.config.AFConfig;
import com.glodblock.github.appflux.xmod.fluxnetwork.FluxNetworkPower;
import com.glodblock.github.appflux.xmod.gtceu.GTEUPower;
import com.gregtechceu.gtceu.api.capability.forge.GTCapability;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sonar.fluxnetworks.api.FluxCapabilities;

import java.util.function.Supplier;

public final class CapAdaptor {

    private static final Reference2ObjectMap<Capability<?>, Factory<?>> CONVERTER = new Reference2ObjectOpenHashMap<>();

    static {
        addCap(ForgeCapabilities.ENERGY, NetworkFEPower::of);
        if (ModList.get().isLoaded("fluxnetworks")) {
            addCap(FluxCapabilities.FN_ENERGY_STORAGE, FluxNetworkPower::of);
        }
        if (ModList.get().isLoaded("gtceu") && AFConfig.gteuSupport()) {
            addCap(GTCapability.CAPABILITY_ENERGY_CONTAINER, GTEUPower::of);
        }
    }

    public static <T> void addCap(Capability<T> cap, Factory<T> factory) {
        CONVERTER.put(cap, factory);
    }

    public static <T> void addCap(Capability<T> cap, NonDataFactory<T> factory) {
        CONVERTER.put(cap, factory);
    }

    @SuppressWarnings("unchecked")
    public static <T> Factory<T> find(Capability<T> cap) {
        return (Factory<T>) CONVERTER.get(cap);
    }

    public interface Factory<T> {

        @NotNull
        T create(@Nullable IStorageService storage, IActionSource source, Supplier<CompoundTag> data);

    }

    public interface NonDataFactory<T> extends Factory<T> {

        @NotNull
        T create(@Nullable IStorageService storage, IActionSource source);

        @NotNull
        default T create(@Nullable IStorageService storage, IActionSource source, Supplier<CompoundTag> data) {
            return this.create(storage, source);
        }

    }

}
