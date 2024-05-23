package com.glodblock.github.extendedae.datagen;

import appeng.datagen.providers.tags.ConventionTags;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.EAEItemAndBlock;
import com.glodblock.github.extendedae.util.EAETags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class EAEItemTagsProvider extends ItemTagsProvider {

    public EAEItemTagsProvider(PackOutput p, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> block, @Nullable ExistingFileHelper existingFileHelper) {
        super(p, lookupProvider, block, ExtendedAE.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(EAETags.EX_PATTERN_PROVIDER)
                .add(EAEItemAndBlock.EX_PATTERN_PROVIDER_PART)
                .add(EAEItemAndBlock.EX_PATTERN_PROVIDER.asItem());
        tag(EAETags.EX_INTERFACE)
                .add(EAEItemAndBlock.EX_INTERFACE_PART)
                .add(EAEItemAndBlock.EX_INTERFACE.asItem());
        tag(Tags.Items.DUSTS)
                .add(EAEItemAndBlock.ENTRO_DUST);
        tag(Tags.Items.GEMS)
                .add(EAEItemAndBlock.ENTRO_CRYSTAL);
        tag(EAETags.ENTRO_CRYSTAL)
                .add(EAEItemAndBlock.ENTRO_CRYSTAL);
        tag(EAETags.ENTRO_DUST)
                .add(EAEItemAndBlock.ENTRO_DUST);
        tag(Tags.Items.INGOTS)
                .add(EAEItemAndBlock.ENTRO_INGOT);
        tag(EAETags.ENTRO_INGOT)
                .add(EAEItemAndBlock.ENTRO_INGOT);
        tag(Tags.Items.STORAGE_BLOCKS)
                .add(EAEItemAndBlock.ENTRO_BLOCK.asItem())
                .add(EAEItemAndBlock.SILICON_BLOCK.asItem());
        tag(EAETags.ENTRO_BLOCK)
                .add(EAEItemAndBlock.ENTRO_BLOCK.asItem());
        tag(ConventionTags.INSCRIBER_PRESSES)
                .add(EAEItemAndBlock.CONCURRENT_PROCESSOR_PRESS);
        tag(ConventionTags.BUDDING_BLOCKS)
                .add(EAEItemAndBlock.FULLY_ENTROIZED_FLUIX_BUDDING.asItem())
                .add(EAEItemAndBlock.MOSTLY_ENTROIZED_FLUIX_BUDDING.asItem())
                .add(EAEItemAndBlock.HALF_ENTROIZED_FLUIX_BUDDING.asItem())
                .add(EAEItemAndBlock.HARDLY_ENTROIZED_FLUIX_BUDDING.asItem());
        tag(ConventionTags.BUDS)
                .add(EAEItemAndBlock.ENTRO_BUD_SMALL.asItem())
                .add(EAEItemAndBlock.ENTRO_BUD_MEDIUM.asItem())
                .add(EAEItemAndBlock.ENTRO_BUD_LARGE.asItem());
        tag(EAETags.SILICON_BLOCK)
                .add(EAEItemAndBlock.SILICON_BLOCK.asItem());
    }
}
