package com.glodblock.github.extendedae.datagen;

import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.EAERegistryHandler;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class EPPBlockTagProvider extends BlockTagsProvider {

    public EPPBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, ExtendedAE.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        TagKey<Block> pickaxe = BlockTags.MINEABLE_WITH_PICKAXE;
        for (var block : EAERegistryHandler.INSTANCE.getBlocks()) {
            tag(pickaxe).add(block);
        }
    }
}
