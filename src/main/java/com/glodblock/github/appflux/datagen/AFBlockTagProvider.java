package com.glodblock.github.appflux.datagen;

import com.glodblock.github.appflux.AppFlux;
import com.glodblock.github.appflux.common.AFSingletons;
import com.glodblock.github.appflux.util.AFTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags.BlockItems;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.registries.DeferredBlock;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class AFBlockTagProvider extends BlockTagsProvider {

    public AFBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, AppFlux.MODID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tagBlock(BlockTags.MINEABLE_WITH_PICKAXE,
                AFSingletons.FLUX_ACCESSOR,
                AFSingletons.CHARGED_REDSTONE_BLOCK);
        tagBlock(BlockItems.STORAGE_BLOCKS.block(),
                AFSingletons.CHARGED_REDSTONE_BLOCK);
        tagBlock(AFTags.CHARGED_REDSTONE_GEM_BLOCK_BLOCK,
                AFSingletons.CHARGED_REDSTONE_BLOCK);
    }

    private void tagBlock(TagKey<Block> tagKey, DeferredBlock<?>... blocks) {
        var tag = tag(tagKey);
        for (DeferredBlock<?> block : blocks) 
            tag.add(BuiltInRegistries.BLOCK.getResourceKey(block.get()).get());
    }
}
