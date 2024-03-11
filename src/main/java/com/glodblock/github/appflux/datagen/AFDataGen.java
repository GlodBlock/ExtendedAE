package com.glodblock.github.appflux.datagen;

import com.glodblock.github.appflux.AppFlux;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@Mod.EventBusSubscriber(modid = AppFlux.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AFDataGen {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent dataEvent) {
        var pack = dataEvent.getGenerator().getVanillaPack(true);
        var file = dataEvent.getExistingFileHelper();
        var lookup = dataEvent.getLookupProvider();
        var block = pack.addProvider(c -> new AFBlockTagProvider(c, lookup, file));
        pack.addProvider(AFLootTableProvider::new);
        pack.addProvider(AFAERecipeProvider::new);
        pack.addProvider(c -> new AFItemTagProvider(c, lookup, block.contentsGetter(), file));
    }

}
