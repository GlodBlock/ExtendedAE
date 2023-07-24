package com.glodblock.github.epp.datagen;

import com.glodblock.github.epp.common.EPPItemAndBlock;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;

public class LootTableGen extends FabricBlockLootTableProvider {

    public LootTableGen(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    protected void generateBlockLootTables() {
        addDrop(EPPItemAndBlock.EX_PATTERN_PROVIDER, drops(EPPItemAndBlock.EX_PATTERN_PROVIDER));
    }

}
