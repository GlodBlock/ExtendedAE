package com.glodblock.github.appflux.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class AFTags {

    public static final TagKey<Item> RESIN_INGOT = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "ingots/insulating_resin"));
    public static final TagKey<Item> REDSTONE_GEM = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "gems/redstone"));

}
