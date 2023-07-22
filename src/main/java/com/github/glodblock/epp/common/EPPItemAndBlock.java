package com.github.glodblock.epp.common;

import com.github.glodblock.epp.common.blocks.BlockExPatternProvider;
import com.github.glodblock.epp.common.items.ItemPartExPatternProvider;
import com.github.glodblock.epp.common.tileentities.TileExPatternProvider;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class EPPItemAndBlock {

    public static final CreativeModeTab TAB = new CreativeModeTab("epp") {
        @Nonnull
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(EX_PATTERN_PROVIDER);
        }
    };

    public static BlockExPatternProvider EX_PATTERN_PROVIDER;
    public static ItemPartExPatternProvider EX_PATTERN_PROVIDER_PART;

    public static void init(RegistryHandler regHandler) {
        EX_PATTERN_PROVIDER = new BlockExPatternProvider();
        EX_PATTERN_PROVIDER_PART = new ItemPartExPatternProvider();
        regHandler.block("ex_pattern_provider", EX_PATTERN_PROVIDER, TileExPatternProvider.class, TileExPatternProvider::new);
        regHandler.item("ex_pattern_provider_part", EX_PATTERN_PROVIDER_PART);
    }

}
