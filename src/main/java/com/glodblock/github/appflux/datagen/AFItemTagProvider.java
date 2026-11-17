package com.glodblock.github.appflux.datagen;

import com.glodblock.github.appflux.AppFlux;
import com.glodblock.github.appflux.common.AFSingletons;
import com.glodblock.github.appflux.util.AFTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.Tags.BlockItems;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class AFItemTagProvider extends ItemTagsProvider {

    public AFItemTagProvider(PackOutput p, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(p, lookupProvider, AppFlux.MODID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tagItem(AFTags.RESIN_INGOT, AFSingletons.HARDEN_INSULATING_RESIN);
        tagItem (Tags.Items.INGOTS, AFSingletons.HARDEN_INSULATING_RESIN);
        tagItem (AFTags.REDSTONE_GEM, AFSingletons.REDSTONE_CRYSTAL);
        tagItem (Tags.Items.GEMS, AFSingletons.REDSTONE_CRYSTAL);
        tagItem (Tags.Items.GEMS, AFSingletons.CHARGED_REDSTONE);
        tagItem (AFTags.CHARGED_REDSTONE_GEM, AFSingletons.CHARGED_REDSTONE);
        tagItem (AFTags.DIAMOND_DUST, AFSingletons.DIAMOND_DUST);
        tagItem (Tags.Items.DUSTS, AFSingletons.DIAMOND_DUST);
        tagItem (AFTags.EMERALD_DUST, AFSingletons.EMERALD_DUST);
        tagItem (Tags.Items.DUSTS, AFSingletons.EMERALD_DUST);
        tagItem (BlockItems.STORAGE_BLOCKS.item(), AFSingletons.CHARGED_REDSTONE_BLOCK);
        tagItem (AFTags.CHARGED_REDSTONE_GEM_BLOCK, AFSingletons.CHARGED_REDSTONE_BLOCK);
    }

    private void tagItem(TagKey<Item> tagKey, ItemLike... items) {
        var tag = tag(tagKey);
        for (ItemLike item : items)
            tag.add(BuiltInRegistries.ITEM.getResourceKey(item.asItem()).get());
    }
}