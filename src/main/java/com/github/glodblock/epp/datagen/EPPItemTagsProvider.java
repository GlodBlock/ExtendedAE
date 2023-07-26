package com.github.glodblock.epp.datagen;

import com.github.glodblock.epp.EPP;
import com.github.glodblock.epp.common.EPPItemAndBlock;
import com.github.glodblock.epp.util.EPPTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class EPPItemTagsProvider extends ItemTagsProvider {

    public EPPItemTagsProvider(PackOutput p, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> block, @Nullable ExistingFileHelper existingFileHelper) {
        super(p, lookupProvider, block, EPP.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(EPPTags.EX_PATTERN_PROVIDER)
                .add(EPPItemAndBlock.EX_PATTERN_PROVIDER_PART)
                .add(EPPItemAndBlock.EX_PATTERN_PROVIDER.asItem());
        tag(EPPTags.EX_INTERFACE)
                .add(EPPItemAndBlock.EX_INTERFACE_PART)
                .add(EPPItemAndBlock.EX_INTERFACE.asItem());
    }
}
