package com.glodblock.github.extendedae.datagen;

import com.glodblock.github.extendedae.ExtendedAE;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.data.event.GatherDataRegistryEntriesEvent;

@EventBusSubscriber(modid = ExtendedAE.MODID)
public class EAEDataGen {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent.Client dataEvent) {
        var pack = dataEvent.getGenerator().getVanillaPack(true);
        var lookup = dataEvent.getReloadableLookupProvider();
        var blockTags = pack.addProvider(c -> new EAEBlockTagProvider(c, lookup));
        pack.addProvider(c -> new EAEItemTagsProvider(c, lookup, blockTags.contentsGetter()));
        pack.addProvider(c -> new EAEComponentTagProvider(c, lookup));
    }

    @SubscribeEvent
    public static void gatherRegistries(GatherDataRegistryEntriesEvent event) {
        event.lootTable(new LootTableProvider.SubProviderEntry(
                EAELootTableProvider.SubProvider::new, LootContextParamSets.BLOCK));
        event.recipe(EAERecipeProvider::new);
    }
}
