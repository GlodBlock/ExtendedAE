package com.glodblock.github.extendedae.datagen;

import appeng.core.ConventionTags;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.util.EAETags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.Tags.BlockItems;
import net.neoforged.neoforge.common.data.BlockTagCopyingItemTagProvider;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class EAEItemTagsProvider extends BlockTagCopyingItemTagProvider {
    public EAEItemTagsProvider(PackOutput output,
        CompletableFuture<HolderLookup.Provider> registries,
        CompletableFuture<TagsProvider.TagLookup<Block>> blockTagsProvider) {
        super(output, registries, blockTagsProvider, ExtendedAE.MODID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tagItem(EAETags.EX_PATTERN_PROVIDER,
                EAESingletons.EX_PATTERN_PROVIDER_PART,
                EAESingletons.EX_PATTERN_PROVIDER);
        tagItem(EAETags.EX_INTERFACE,
                EAESingletons.EX_INTERFACE_PART,
                EAESingletons.EX_INTERFACE);
        tagItem(EAETags.OVERSIZE_INTERFACE,
                EAESingletons.OVERSIZE_INTERFACE_PART,
                EAESingletons.OVERSIZE_INTERFACE);
        tagItem(Tags.Items.DUSTS,
                EAESingletons.ENTRO_DUST,
                EAESingletons.QUARTZ_BLEND);
        tagItem(Tags.Items.GEMS,
                EAESingletons.ENTRO_CRYSTAL);
        tagItem(EAETags.ENTRO_CRYSTAL,
                EAESingletons.ENTRO_CRYSTAL);
        tagItem(EAETags.ENTRO_DUST,
                EAESingletons.ENTRO_DUST);
        tagItem(Tags.Items.INGOTS,
                EAESingletons.ENTRO_INGOT);
        tagItem(EAETags.ENTRO_INGOT,
                EAESingletons.ENTRO_INGOT);
        tagItem(BlockItems.STORAGE_BLOCKS.item(),
                EAESingletons.ENTRO_BLOCK,
                EAESingletons.SILICON_BLOCK);
        tagItem(EAETags.ENTRO_BLOCK,
                EAESingletons.ENTRO_BLOCK);
        tagItem(ConventionTags.BUDDING_BLOCKS,
                EAESingletons.FULLY_ENTROIZED_FLUIX_BUDDING,
                EAESingletons.MOSTLY_ENTROIZED_FLUIX_BUDDING,
                EAESingletons.HALF_ENTROIZED_FLUIX_BUDDING,
                EAESingletons.HARDLY_ENTROIZED_FLUIX_BUDDING);
        tagItem(ConventionTags.BUDS,
                EAESingletons.ENTRO_BUD_SMALL,
                EAESingletons.ENTRO_BUD_MEDIUM,
                EAESingletons.ENTRO_BUD_LARGE);
        tagItem(EAETags.SILICON_BLOCK,
                EAESingletons.SILICON_BLOCK);
        tagItem(ItemTags.DURABILITY_ENCHANTABLE,
                EAESingletons.SMART_ANNIHILATION_PLANE);
        tagItem(ItemTags.MINING_ENCHANTABLE,
                EAESingletons.SMART_ANNIHILATION_PLANE);
        tagItem(ItemTags.MINING_LOOT_ENCHANTABLE,
                EAESingletons.SMART_ANNIHILATION_PLANE);
    }

    private void tagItem(TagKey<Item> tagKey, ItemLike... items) {
        var tag = tag(tagKey);
        for (ItemLike item : items)
                tag.add(BuiltInRegistries.ITEM.getResourceKey(item.asItem()).get());
    }
}
