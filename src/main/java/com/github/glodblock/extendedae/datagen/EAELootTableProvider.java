package com.github.glodblock.extendedae.datagen;

import com.github.glodblock.extendedae.common.RegistryHandler;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;

public class EAELootTableProvider extends FabricBlockLootTableProvider {

    public EAELootTableProvider(FabricDataOutput p) {
        super(p);
    }

    @Override
    public void generate() {
        for (var block : RegistryHandler.INSTANCE.getBlocks()) {
            dropSelf(block);
        }
    }

}
