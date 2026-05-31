package com.glodblock.github.extendedae.datagen;

import appeng.api.ids.AETags;
import appeng.core.ConventionTags;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.api.OptionalBlock;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.common.EAERegistryHandler;
import com.glodblock.github.extendedae.util.EAETags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.Tags;
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
                tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block);
            }
        }
        tag(AETags.GROWTH_ACCELERATABLE)
                .add(EAESingletons.FULLY_ENTROIZED_FLUIX_BUDDING.get())
                .add(EAESingletons.MOSTLY_ENTROIZED_FLUIX_BUDDING.get())
                .add(EAESingletons.HALF_ENTROIZED_FLUIX_BUDDING.get())
                .add(EAESingletons.HARDLY_ENTROIZED_FLUIX_BUDDING.get());
        tag(ConventionTags.BUDDING_BLOCKS_BLOCKS)
                .add(EAESingletons.FULLY_ENTROIZED_FLUIX_BUDDING.get())
                .add(EAESingletons.MOSTLY_ENTROIZED_FLUIX_BUDDING.get())
                .add(EAESingletons.HALF_ENTROIZED_FLUIX_BUDDING.get())
                .add(EAESingletons.HARDLY_ENTROIZED_FLUIX_BUDDING.get());
        tag(ConventionTags.BUDS_BLOCKS)
                .add(EAESingletons.ENTRO_BUD_SMALL.get())
                .add(EAESingletons.ENTRO_BUD_MEDIUM.get())
                .add(EAESingletons.ENTRO_BUD_LARGE.get());
        tag(Tags.Blocks.STORAGE_BLOCKS)
                .add(EAESingletons.ENTRO_BLOCK.get())
                .add(EAESingletons.SILICON_BLOCK.get());
        tag(EAETags.ENTRO_BLOCK_BLOCK)
                .add(EAESingletons.ENTRO_BLOCK.get());
        tag(EAETags.SILICON_BLOCK_BLOCK)
                .add(EAESingletons.SILICON_BLOCK.get());
    }
}
