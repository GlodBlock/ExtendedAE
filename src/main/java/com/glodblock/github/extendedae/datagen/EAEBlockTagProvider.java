package com.glodblock.github.extendedae.datagen;

import appeng.api.ids.AETags;
import appeng.core.ConventionTags;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.api.OptionalBlock;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.common.EAERegistryHandler;
import com.glodblock.github.extendedae.util.EAETags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags.BlockItems;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class EAEBlockTagProvider extends BlockTagsProvider {

    public EAEBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, ExtendedAE.MODID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        for (var block : EAERegistryHandler.INSTANCE.getBlocks()) {
            if (!(block instanceof OptionalBlock)) {
                tagBlock(BlockTags.MINEABLE_WITH_PICKAXE, block);
            }
        }
        tagBlock(AETags.GROWTH_ACCELERATABLE,
                EAESingletons.FULLY_ENTROIZED_FLUIX_BUDDING.get(),
                EAESingletons.MOSTLY_ENTROIZED_FLUIX_BUDDING.get(),
                EAESingletons.HALF_ENTROIZED_FLUIX_BUDDING.get(),
                EAESingletons.HARDLY_ENTROIZED_FLUIX_BUDDING.get());
        tagBlock(ConventionTags.BUDDING_BLOCKS_BLOCKS,
                EAESingletons.FULLY_ENTROIZED_FLUIX_BUDDING.get(),
                EAESingletons.MOSTLY_ENTROIZED_FLUIX_BUDDING.get(),
                EAESingletons.HALF_ENTROIZED_FLUIX_BUDDING.get(),
                EAESingletons.HARDLY_ENTROIZED_FLUIX_BUDDING.get());
        tagBlock(ConventionTags.BUDS_BLOCKS,
                EAESingletons.ENTRO_BUD_SMALL.get(),
                EAESingletons.ENTRO_BUD_MEDIUM.get(),
                EAESingletons.ENTRO_BUD_LARGE.get());
        tagBlock(BlockItems.STORAGE_BLOCKS.block(),
                EAESingletons.ENTRO_BLOCK.get(),
                EAESingletons.SILICON_BLOCK.get());
        tagBlock(EAETags.ENTRO_BLOCK_BLOCK,
                EAESingletons.ENTRO_BLOCK.get());
        tagBlock(EAETags.SILICON_BLOCK_BLOCK,
                EAESingletons.SILICON_BLOCK.get());
    }

    private void tagBlock(TagKey<Block> tagKey, Block... blocks) {
        var tag = tag(tagKey);
        for (Block block : blocks)
                tag.add(BuiltInRegistries.BLOCK.getResourceKey(block).get());
    }
}
