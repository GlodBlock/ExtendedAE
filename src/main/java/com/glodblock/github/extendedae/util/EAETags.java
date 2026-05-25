package com.glodblock.github.extendedae.util;

import com.glodblock.github.extendedae.ExtendedAE;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class EAETags {

    public static final TagKey<@NotNull Item> EX_PATTERN_PROVIDER = TagKey.create(Registries.ITEM, ExtendedAE.id("extended_pattern_provider"));
    public static final TagKey<@NotNull Item> EX_INTERFACE = TagKey.create(Registries.ITEM, ExtendedAE.id("extended_interface"));
    public static final TagKey<@NotNull Item> ENTRO_DUST = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "dusts/entro"));
    public static final TagKey<@NotNull Item> ENTRO_CRYSTAL = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "gems/entro"));
    public static final TagKey<@NotNull Item> ENTRO_INGOT = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "ingots/infused_entro"));
    public static final TagKey<@NotNull Item> ENTRO_BLOCK = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "storage_blocks/entro"));
    public static final TagKey<@NotNull Item> SILICON_BLOCK = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "storage_blocks/silicon"));
    public static final TagKey<@NotNull Item> OVERSIZE_INTERFACE = TagKey.create(Registries.ITEM, ExtendedAE.id("oversize_interface"));
    public static final TagKey<@NotNull Item> QUARTZ_DUST = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "dusts/quartz"));
    public static final TagKey<@NotNull Item> EX_EMC_INTERFACE = TagKey.create(Registries.ITEM, ExtendedAE.id("extended_emc_interface"));

    public static final TagKey<@NotNull Block> ENTRO_BLOCK_BLOCK = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("c", "storage_blocks/entro"));
    public static final TagKey<@NotNull Block> SILICON_BLOCK_BLOCK = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("c", "storage_blocks/silicon"));

}
