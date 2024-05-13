package com.glodblock.github.extendedae.datagen;

import com.glodblock.github.extendedae.ExtendedAE;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@Mod.EventBusSubscriber(modid = ExtendedAE.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EPPDataGen {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent dataEvent) {
        var pack = dataEvent.getGenerator().getVanillaPack(true);
        var file = dataEvent.getExistingFileHelper();
        var lookup = dataEvent.getLookupProvider();
        var blockTagsProvider = pack
                .addProvider(c -> new EPPBlockTagProvider(c, lookup, file));
        pack.addProvider(EPPRecipeProvider::new);
        pack.addProvider(EPPLootTableProvider::new);
        pack.addProvider(c -> new EPPItemTagsProvider(c, lookup, blockTagsProvider.contentsGetter(), file));
    }

}
