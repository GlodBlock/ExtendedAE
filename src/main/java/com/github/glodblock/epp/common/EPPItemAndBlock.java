package com.github.glodblock.epp.common;

import com.github.glodblock.epp.common.blocks.BlockExPatternProvider;
import com.github.glodblock.epp.common.items.ItemPartExPatternProvider;
import com.github.glodblock.epp.common.items.ItemPatternProviderUpgrade;
import com.github.glodblock.epp.common.tileentities.TileExPatternProvider;

public class EPPItemAndBlock {

    public static BlockExPatternProvider EX_PATTERN_PROVIDER;
    public static ItemPartExPatternProvider EX_PATTERN_PROVIDER_PART;
    public static ItemPatternProviderUpgrade PATTERN_PROVIDER_UPGRADE;

    public static void init(RegistryHandler regHandler) {
        EX_PATTERN_PROVIDER = new BlockExPatternProvider();
        EX_PATTERN_PROVIDER_PART = new ItemPartExPatternProvider();
        PATTERN_PROVIDER_UPGRADE = new ItemPatternProviderUpgrade();
        regHandler.block("ex_pattern_provider", EX_PATTERN_PROVIDER, TileExPatternProvider.class, TileExPatternProvider::new);
        regHandler.item("ex_pattern_provider_part", EX_PATTERN_PROVIDER_PART);
        regHandler.item("pattern_provider_upgrade", PATTERN_PROVIDER_UPGRADE);
    }

}
