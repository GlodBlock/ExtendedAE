package com.glodblock.github.appflux.datagen;

import com.glodblock.github.appflux.AppFlux;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.data.event.GatherDataRegistryEntriesEvent;

@EventBusSubscriber(modid = AppFlux.MODID)
public class AFDataGen {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent.Client dataEvent) {
        var pack = dataEvent.getGenerator().getVanillaPack(true);
        var lookup = dataEvent.getReloadableLookupProvider();
        pack.addProvider(c -> new AFBlockTagProvider(c, lookup));
        pack.addProvider(c -> new AFItemTagProvider(c, lookup));
    }

    @SubscribeEvent
    public static void gatherRegistries(GatherDataRegistryEntriesEvent event) {
        event.lootTable(new LootTableProvider.SubProviderEntry(
                AFLootTableProvider.SubProvider::new, LootContextParamSets.BLOCK));
        event.recipe(AFRecipeProvider::new);
    }
}
