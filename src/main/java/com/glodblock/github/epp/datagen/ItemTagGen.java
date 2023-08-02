package com.glodblock.github.epp.datagen;

import com.glodblock.github.epp.common.EPPItemAndBlock;
import com.glodblock.github.epp.util.EPPTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import org.jetbrains.annotations.Nullable;

public class ItemTagGen extends FabricTagProvider.ItemTagProvider {

    public ItemTagGen(FabricDataGenerator dataGenerator, @Nullable BlockTagProvider blockTagProvider) {
        super(dataGenerator, blockTagProvider);
    }

    @Override
    protected void generateTags() {
        this.getOrCreateTagBuilder(EPPTags.EX_PATTERN_PROVIDER)
                .add(EPPItemAndBlock.EX_PATTERN_PROVIDER_PART)
                .add(EPPItemAndBlock.EX_PATTERN_PROVIDER.asItem());
        this.getOrCreateTagBuilder(EPPTags.EX_INTERFACE)
                .add(EPPItemAndBlock.EX_INTERFACE_PART)
                .add(EPPItemAndBlock.EX_INTERFACE.asItem());
    }
}
