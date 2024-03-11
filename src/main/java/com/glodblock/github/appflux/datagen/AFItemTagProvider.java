package com.glodblock.github.appflux.datagen;

import com.glodblock.github.appflux.AppFlux;
import com.glodblock.github.appflux.common.AFItemAndBlock;
import com.glodblock.github.appflux.util.AFTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class AFItemTagProvider extends ItemTagsProvider {

    public AFItemTagProvider(PackOutput p, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup< Block >> block, @Nullable ExistingFileHelper existingFileHelper) {
        super(p, lookupProvider, block, AppFlux.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(AFTags.RESIN_INGOT).add(AFItemAndBlock.HARDEN_INSULATING_RESIN);
        tag(Tags.Items.INGOTS).add(AFItemAndBlock.HARDEN_INSULATING_RESIN);
        tag(AFTags.REDSTONE_GEM).add(AFItemAndBlock.REDSTONE_CRYSTAL);
        tag(Tags.Items.GEMS).add(AFItemAndBlock.REDSTONE_CRYSTAL);
        tag(AFTags.DIAMOND_DUST).add(AFItemAndBlock.DIAMOND_DUST);
        tag(Tags.Items.DUSTS).add(AFItemAndBlock.DIAMOND_DUST);
        tag(AFTags.EMERALD_DUST).add(AFItemAndBlock.EMERALD_DUST);
        tag(Tags.Items.DUSTS).add(AFItemAndBlock.EMERALD_DUST);
    }
}