package com.glodblock.github.extendedae.datagen;

import com.glodblock.github.extendedae.ExtendedAE;
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

@EventBusSubscriber(modid = ExtendedAE.MODID)
public class EAEDataGen {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent.Client dataEvent) {
        var pack = dataEvent.getGenerator().getVanillaPack(true);
        var lookup = dataEvent.getLookupProvider();
        CompletableFuture<HolderLookup.Provider> registries = CompletableFuture.supplyAsync(VanillaRegistries::createLookup, Util.backgroundExecutor());
        pack.addProvider(bindRegistries(EAERecipeProvider.Runner::new, registries));
        pack.addProvider(p -> new EAELootTableProvider(p, lookup));
        pack.addProvider(c -> new EAEBlockTagProvider(c, lookup));
        pack.addProvider(c -> new EAEItemTagsProvider(c, lookup));
        pack.addProvider(c -> new EAEComponentTagProvider(c, lookup));
    }

    private static <T extends DataProvider> DataProvider.Factory<@NotNull T> bindRegistries(BiFunction<PackOutput, CompletableFuture<HolderLookup.Provider>, T> target, CompletableFuture<HolderLookup.Provider> registries) {
        return output -> target.apply(output, registries);
    }

}
