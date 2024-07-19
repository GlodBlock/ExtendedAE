package com.glodblock.github.appflux.datagen;

import com.glodblock.github.appflux.AppFlux;
import com.glodblock.github.appflux.common.AFSingletons;
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
        tag(AFTags.RESIN_INGOT).add(AFSingletons.HARDEN_INSULATING_RESIN);
        tag(Tags.Items.INGOTS).add(AFSingletons.HARDEN_INSULATING_RESIN);
        tag(AFTags.REDSTONE_GEM).add(AFSingletons.REDSTONE_CRYSTAL);
        tag(Tags.Items.GEMS).add(AFSingletons.REDSTONE_CRYSTAL);
        tag(AFTags.DIAMOND_DUST).add(AFSingletons.DIAMOND_DUST);
        tag(Tags.Items.DUSTS).add(AFSingletons.DIAMOND_DUST);
        tag(AFTags.EMERALD_DUST).add(AFSingletons.EMERALD_DUST);
        tag(Tags.Items.DUSTS).add(AFSingletons.EMERALD_DUST);
        tag(Tags.Items.STORAGE_BLOCKS).add(AFSingletons.CHARGED_REDSTONE_BLOCK.asItem());
    }
}