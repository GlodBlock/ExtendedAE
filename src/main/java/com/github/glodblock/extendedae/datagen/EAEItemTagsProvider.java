package com.github.glodblock.extendedae.datagen;

import com.github.glodblock.extendedae.common.EAEItemAndBlock;
import com.github.glodblock.extendedae.util.EPPTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class EAEItemTagsProvider extends FabricTagProvider.ItemTagProvider {

    public EAEItemTagsProvider(FabricDataOutput packOutput, CompletableFuture<HolderLookup.Provider> registries, FabricTagProvider.BlockTagProvider blockTagsProvider) {
        super(packOutput, registries, blockTagsProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        this.getOrCreateTagBuilder(EPPTags.EX_PATTERN_PROVIDER)
                .add(EAEItemAndBlock.EX_PATTERN_PROVIDER_PART)
                .add(EAEItemAndBlock.EX_PATTERN_PROVIDER.asItem());
        this.getOrCreateTagBuilder(EPPTags.EX_INTERFACE)
                .add(EAEItemAndBlock.EX_INTERFACE_PART)
                .add(EAEItemAndBlock.EX_INTERFACE.asItem());
    }
}
