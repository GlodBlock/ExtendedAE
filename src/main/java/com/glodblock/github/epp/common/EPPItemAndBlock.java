package com.glodblock.github.epp.common;

import com.glodblock.github.epp.EPP;
import com.glodblock.github.epp.common.blocks.BlockExPatternProvider;
import com.glodblock.github.epp.common.items.ItemPartExPatternProvider;
import com.glodblock.github.epp.common.tiles.TileExPatternProvider;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class EPPItemAndBlock {

    public static BlockExPatternProvider EX_PATTERN_PROVIDER;
    public static ItemPartExPatternProvider EX_PATTERN_PROVIDER_PART;
    public static final ItemGroup TAB = FabricItemGroupBuilder.build(EPP.id("eppitems"), () -> new ItemStack(EX_PATTERN_PROVIDER));

    public static void init(RegistryHandler regHandler) {
        EX_PATTERN_PROVIDER = new BlockExPatternProvider();
        EX_PATTERN_PROVIDER_PART = new ItemPartExPatternProvider();
        regHandler.block("ex_pattern_provider", EX_PATTERN_PROVIDER, TileExPatternProvider.class, TileExPatternProvider.TYPE);
        regHandler.item("ex_pattern_provider_part", EX_PATTERN_PROVIDER_PART);
    }

}
