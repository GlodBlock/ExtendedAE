package com.glodblock.github.appflux.datagen;

import com.glodblock.github.appflux.AppFlux;
import com.glodblock.github.appflux.common.AFSingletons;
import com.glodblock.github.appflux.util.AFTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class AFBlockTagProvider extends BlockTagsProvider {

    public AFBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, AppFlux.MODID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(AFSingletons.FLUX_ACCESSOR.get())
                .add(AFSingletons.CHARGED_REDSTONE_BLOCK.get());
        tag(Tags.Blocks.STORAGE_BLOCKS)
                .add(AFSingletons.CHARGED_REDSTONE_BLOCK.get());
        tag(AFTags.CHARGED_REDSTONE_GEM_BLOCK_BLOCK)
                .add(AFSingletons.CHARGED_REDSTONE_BLOCK.get());
    }
}
