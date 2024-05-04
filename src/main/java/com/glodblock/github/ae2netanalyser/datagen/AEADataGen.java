package com.glodblock.github.ae2netanalyser.datagen;

import com.glodblock.github.ae2netanalyser.AEAnalyser;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@Mod.EventBusSubscriber(modid = AEAnalyser.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AEADataGen {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent dataEvent) {
        var pack = dataEvent.getGenerator().getVanillaPack(true);
        pack.addProvider(AEARecipeProvider::new);
    }

}
