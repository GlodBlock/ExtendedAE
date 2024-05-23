package com.glodblock.github.extendedae.util;

import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.glodium.Glodium;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class EAETags {

    public static final TagKey<Item> EX_PATTERN_PROVIDER = TagKey.create(Registries.ITEM, ExtendedAE.id("extended_pattern_provider"));
    public static final TagKey<Item> EX_INTERFACE = TagKey.create(Registries.ITEM, ExtendedAE.id("extended_interface"));
    public static final TagKey<Item> ENTRO_DUST = TagKey.create(Registries.ITEM, Glodium.id("forge", "dusts/entro"));
    public static final TagKey<Item> ENTRO_CRYSTAL = TagKey.create(Registries.ITEM, Glodium.id("forge", "gems/entro"));
    public static final TagKey<Item> ENTRO_INGOT = TagKey.create(Registries.ITEM, Glodium.id("forge", "ingots/infused_entro"));
    public static final TagKey<Item> ENTRO_BLOCK = TagKey.create(Registries.ITEM, Glodium.id("forge", "storage_blocks/entro"));
    public static final TagKey<Item> SILICON_BLOCK = TagKey.create(Registries.ITEM, Glodium.id("forge", "storage_blocks/silicon"));

}
