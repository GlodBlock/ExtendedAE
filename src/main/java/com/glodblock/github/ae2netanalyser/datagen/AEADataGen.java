package com.glodblock.github.ae2netanalyser.datagen;

import com.glodblock.github.ae2netanalyser.AEAnalyser;
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

@EventBusSubscriber(modid = AEAnalyser.MODID)
public class AEADataGen {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent.Client dataEvent) {
        var pack = dataEvent.getGenerator().getVanillaPack(true);
        CompletableFuture<HolderLookup.Provider> registries = CompletableFuture.supplyAsync(VanillaRegistries::createLookup, Util.backgroundExecutor());
        pack.addProvider(bindRegistries(AEARecipeProvider.Runner::new, registries));
    }

    private static <T extends DataProvider> DataProvider.Factory<@NotNull T> bindRegistries(BiFunction<PackOutput, CompletableFuture<HolderLookup.Provider>, T> target, CompletableFuture<HolderLookup.Provider> registries) {
        return output -> target.apply(output, registries);
    }

}
