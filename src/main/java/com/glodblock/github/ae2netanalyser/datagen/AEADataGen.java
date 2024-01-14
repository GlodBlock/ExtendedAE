package com.glodblock.github.ae2netanalyser.datagen;

import com.glodblock.github.ae2netanalyser.AEAnalyser;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AEAnalyser.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AEADataGen {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent dataEvent) {
        var pack = dataEvent.getGenerator().getVanillaPack(true);
        pack.addProvider(AEARecipeProvider::new);
    }

}
