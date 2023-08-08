package com.github.glodblock.eterminal.common;

import appeng.items.parts.PartItem;
import com.github.glodblock.eterminal.common.parts.PartExPatternAccessTerminal;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class ETerminalItemAndBlock {

    public static final CreativeModeTab TAB = new CreativeModeTab("eterminal") {
        @Nonnull
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(EX_PATTERN_TERMINAL);
        }
    };

    public static PartItem<PartExPatternAccessTerminal> EX_PATTERN_TERMINAL;

    public static void init(RegistryHandler regHandler) {
        EX_PATTERN_TERMINAL = new PartItem<>(new Item.Properties().tab(TAB), PartExPatternAccessTerminal.class, PartExPatternAccessTerminal::new);
        regHandler.item("ex_pattern_access_part", EX_PATTERN_TERMINAL);
    }

}
