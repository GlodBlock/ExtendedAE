package com.github.glodblock.extendedae.datagen;

import com.github.glodblock.extendedae.common.RegistryHandler;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;

import java.util.concurrent.CompletableFuture;

public class EAEBlockTagProvider extends FabricTagProvider.BlockTagProvider {

    public EAEBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        var pickaxe = this.getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_PICKAXE);
        for (var block : RegistryHandler.INSTANCE.getBlocks()) {
            pickaxe.add(block);
        }
    }
}
