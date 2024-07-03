package com.glodblock.github.extendedae.datagen;

import com.glodblock.github.extendedae.ExtendedAE;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = ExtendedAE.MODID, bus = EventBusSubscriber.Bus.MOD)
public class EAEDataGen {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent dataEvent) {
        var pack = dataEvent.getGenerator().getVanillaPack(true);
        var file = dataEvent.getExistingFileHelper();
        var lookup = dataEvent.getLookupProvider();
        var blockTagsProvider = pack.addProvider(c -> new EAEBlockTagProvider(c, lookup, file));
        pack.addProvider(p -> new EAERecipeProvider(p, lookup));
        pack.addProvider(p -> new EAELootTableProvider(p, lookup));
        pack.addProvider(c -> new EAEItemTagsProvider(c, lookup, blockTagsProvider.contentsGetter(), file));
        pack.addProvider(c -> new EAEComponentTagProvider(c, lookup, file));
    }

}
