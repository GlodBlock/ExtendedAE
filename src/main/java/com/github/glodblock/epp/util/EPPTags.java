package com.github.glodblock.epp.util;

import com.github.glodblock.epp.EPP;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class EPPTags {

    public static final TagKey<Item> EX_PATTERN_PROVIDER = TagKey.create(Registry.ITEM_REGISTRY, EPP.id("extended_pattern_provider"));
    public static final TagKey<Item> EX_INTERFACE = TagKey.create(Registry.ITEM_REGISTRY, EPP.id("extended_interface"));

}
