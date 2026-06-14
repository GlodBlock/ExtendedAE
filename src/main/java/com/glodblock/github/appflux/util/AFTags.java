package com.glodblock.github.appflux.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class AFTags {

    public static final TagKey<@NotNull Item> RESIN_INGOT = TagKey.create(Registries.ITEM, Identifier.parse("c:ingots/insulating_resin"));
    public static final TagKey<@NotNull Item> REDSTONE_GEM = TagKey.create(Registries.ITEM, Identifier.parse("c:gems/redstone"));
    public static final TagKey<@NotNull Item> CHARGED_REDSTONE_GEM = TagKey.create(Registries.ITEM, Identifier.parse("c:gems/charged_redstone"));
    public static final TagKey<@NotNull Item> CHARGED_REDSTONE_GEM_BLOCK = TagKey.create(Registries.ITEM, Identifier.parse("c:storage_blocks/charged_redstone"));
    public static final TagKey<@NotNull Item> DIAMOND_DUST = TagKey.create(Registries.ITEM, Identifier.parse("c:dusts/diamond"));
    public static final TagKey<@NotNull Item> EMERALD_DUST = TagKey.create(Registries.ITEM, Identifier.parse("c:dusts/emerald"));

    public static final TagKey<@NotNull Block> CHARGED_REDSTONE_GEM_BLOCK_BLOCK = TagKey.create(Registries.BLOCK, Identifier.parse("c:storage_blocks/charged_redstone"));

}
