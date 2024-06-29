package com.glodblock.github.ae2netanalyser.datagen;

import com.glodblock.github.ae2netanalyser.AEAnalyser;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = AEAnalyser.MODID, bus = EventBusSubscriber.Bus.MOD)
public class AEADataGen {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent dataEvent) {
        var pack = dataEvent.getGenerator().getVanillaPack(true);
        var future = dataEvent.getLookupProvider();
        pack.addProvider(p -> new AEARecipeProvider(p, future));
    }

}
