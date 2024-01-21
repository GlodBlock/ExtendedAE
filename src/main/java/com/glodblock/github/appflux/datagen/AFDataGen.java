package com.glodblock.github.appflux.datagen;

import com.glodblock.github.appflux.AppFlux;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AppFlux.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AFDataGen {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent dataEvent) {
        var pack = dataEvent.getGenerator().getVanillaPack(true);
        var file = dataEvent.getExistingFileHelper();
        var lookup = dataEvent.getLookupProvider();
        pack.addProvider(c -> new AFBlockTagProvider(c, lookup, file));
        pack.addProvider(AFLootTableProvider::new);
        pack.addProvider(AFAERecipeProvider::new);
    }

}
