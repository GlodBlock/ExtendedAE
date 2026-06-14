package com.glodblock.github.appflux.datagen;

import com.glodblock.github.appflux.AppFlux;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.util.Util;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

@EventBusSubscriber(modid = AppFlux.MODID)
public class AFDataGen {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent.Client dataEvent) {
        var pack = dataEvent.getGenerator().getVanillaPack(true);
        var lookup = dataEvent.getLookupProvider();
        CompletableFuture<HolderLookup.Provider> registries = CompletableFuture.supplyAsync(VanillaRegistries::createLookup, Util.backgroundExecutor());
        pack.addProvider(bindRegistries(AFRecipeProvider.Runner::new, registries));
        pack.addProvider(c -> new AFBlockTagProvider(c, lookup));
        pack.addProvider(c -> new AFLootTableProvider(c, lookup));
        pack.addProvider(c -> new AFItemTagProvider(c, lookup));
    }

    private static <T extends DataProvider> DataProvider.Factory<@NotNull T> bindRegistries(BiFunction<PackOutput, CompletableFuture<HolderLookup.Provider>, T> target, CompletableFuture<HolderLookup.Provider> registries) {
        return output -> target.apply(output, registries);
    }

}
