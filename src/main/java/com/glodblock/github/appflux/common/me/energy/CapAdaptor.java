package com.glodblock.github.appflux.common.me.energy;

import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageService;
import com.glodblock.github.appflux.common.caps.NetworkFEPower;
import com.glodblock.github.appflux.xmod.fluxnetwork.FluxNetworkPower;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sonar.fluxnetworks.api.FluxCapabilities;

public final class CapAdaptor {

    private static final Reference2ObjectMap<Capability<?>, Factory<?>> CONVERTER = new Reference2ObjectOpenHashMap<>();

    static {
        addCap(ForgeCapabilities.ENERGY, NetworkFEPower::of);
        if (ModList.get().isLoaded("fluxnetworks")) {
            addCap(FluxCapabilities.FN_ENERGY_STORAGE, FluxNetworkPower::of);
        }
    }

    public static <T> void addCap(Capability<T> cap, Factory<T> factory) {
        CONVERTER.put(cap, factory);
    }

    @SuppressWarnings("unchecked")
    public static <T> Factory<T> find(Capability<T> cap) {
        return (Factory<T>) CONVERTER.get(cap);
    }

    public interface Factory<T> {

        @NotNull
        T create(@Nullable IStorageService storage, IActionSource source);

    }

}
