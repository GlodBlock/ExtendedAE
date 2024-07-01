package com.glodblock.github.appflux.datagen;

import com.glodblock.github.appflux.AppFlux;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = AppFlux.MODID, bus = EventBusSubscriber.Bus.MOD)
public class AFDataGen {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent dataEvent) {
        var pack = dataEvent.getGenerator().getVanillaPack(true);
        var file = dataEvent.getExistingFileHelper();
        var lookup = dataEvent.getLookupProvider();
        var block = pack.addProvider(c -> new AFBlockTagProvider(c, lookup, file));
        pack.addProvider(c -> new AFLootTableProvider(c, lookup));
        pack.addProvider(c -> new AFAERecipeProvider(c, lookup));
        pack.addProvider(c -> new AFItemTagProvider(c, lookup, block.contentsGetter(), file));
    }

}
