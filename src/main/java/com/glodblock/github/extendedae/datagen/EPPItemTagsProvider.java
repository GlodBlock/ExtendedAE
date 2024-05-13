package com.glodblock.github.extendedae.datagen;

import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.EAEItemAndBlock;
import com.glodblock.github.extendedae.util.EPPTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class EPPItemTagsProvider extends ItemTagsProvider {

    public EPPItemTagsProvider(PackOutput p, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> block, @Nullable ExistingFileHelper existingFileHelper) {
        super(p, lookupProvider, block, ExtendedAE.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(EPPTags.EX_PATTERN_PROVIDER)
                .add(EAEItemAndBlock.EX_PATTERN_PROVIDER_PART)
                .add(EAEItemAndBlock.EX_PATTERN_PROVIDER.asItem());
        tag(EPPTags.EX_INTERFACE)
                .add(EAEItemAndBlock.EX_INTERFACE_PART)
                .add(EAEItemAndBlock.EX_INTERFACE.asItem());
    }
}
