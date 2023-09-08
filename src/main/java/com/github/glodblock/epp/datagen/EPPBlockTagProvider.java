package com.github.glodblock.epp.datagen;

import com.github.glodblock.epp.EPP;
import com.github.glodblock.epp.common.EPPItemAndBlock;
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
        super(output, lookupProvider, EPP.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        TagKey<Block> pickaxe = BlockTags.MINEABLE_WITH_PICKAXE;
        tag(pickaxe)
                .add(EPPItemAndBlock.EX_PATTERN_PROVIDER)
                .add(EPPItemAndBlock.EX_INTERFACE)
                .add(EPPItemAndBlock.WIRELESS_CONNECTOR)
                .add(EPPItemAndBlock.INGREDIENT_BUFFER)
                .add(EPPItemAndBlock.EX_DRIVE);
    }
}
