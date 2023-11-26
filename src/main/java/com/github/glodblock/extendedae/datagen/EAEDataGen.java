package com.github.glodblock.extendedae.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class EAEDataGen implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        var pack = fabricDataGenerator.createPack();
        var BTP = pack.addProvider(EAEBlockTagProvider::new);
        pack.addProvider(EAELootTableProvider::new);
        pack.addProvider(EAERecipeProvider::new);
        pack.addProvider((a, b) -> new EAEItemTagsProvider(a, b, BTP));
    }

}
