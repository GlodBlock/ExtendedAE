package com.glodblock.github.appflux.datagen;

import com.glodblock.github.appflux.AppFlux;
import com.glodblock.github.appflux.common.AFSingletons;
import com.glodblock.github.appflux.util.AFTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class AFItemTagProvider extends ItemTagsProvider {

    public AFItemTagProvider(PackOutput p, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(p, lookupProvider, AppFlux.MODID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(AFTags.RESIN_INGOT).add(AFSingletons.HARDEN_INSULATING_RESIN.get());
        tag(Tags.Items.INGOTS).add(AFSingletons.HARDEN_INSULATING_RESIN.get());
        tag(AFTags.REDSTONE_GEM).add(AFSingletons.REDSTONE_CRYSTAL.get());
        tag(Tags.Items.GEMS).add(AFSingletons.REDSTONE_CRYSTAL.get());
        tag(Tags.Items.GEMS).add(AFSingletons.CHARGED_REDSTONE.get());
        tag(AFTags.CHARGED_REDSTONE_GEM).add(AFSingletons.CHARGED_REDSTONE.get());
        tag(AFTags.DIAMOND_DUST).add(AFSingletons.DIAMOND_DUST.get());
        tag(Tags.Items.DUSTS).add(AFSingletons.DIAMOND_DUST.get());
        tag(AFTags.EMERALD_DUST).add(AFSingletons.EMERALD_DUST.get());
        tag(Tags.Items.DUSTS).add(AFSingletons.EMERALD_DUST.get());
        tag(Tags.Items.STORAGE_BLOCKS).add(AFSingletons.CHARGED_REDSTONE_BLOCK.asItem());
        tag(AFTags.CHARGED_REDSTONE_GEM_BLOCK).add(AFSingletons.CHARGED_REDSTONE_BLOCK.asItem());
    }
}