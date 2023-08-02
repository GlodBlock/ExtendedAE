package com.glodblock.github.epp.util;

import com.glodblock.github.epp.EPP;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.Registry;

public class EPPTags {

    public static final TagKey<Item> EX_PATTERN_PROVIDER = TagKey.of(Registry.ITEM.getKey(), EPP.id("extended_pattern_provider"));
    public static final TagKey<Item> EX_INTERFACE = TagKey.of(Registry.ITEM.getKey(), EPP.id("extended_interface"));


}
