package com.glodblock.github.appflux.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class AFTags {

    public static final TagKey<Item> RESIN_INGOT = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:ingots/insulating_resin"));
    public static final TagKey<Item> REDSTONE_GEM = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:gems/redstone"));
    public static final TagKey<Item> DIAMOND_DUST = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:dusts/diamond"));
    public static final TagKey<Item> EMERALD_DUST = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:dusts/emerald"));

}
