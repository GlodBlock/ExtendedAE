package com.github.glodblock.epp.common.items;

import appeng.parts.reporting.PatternAccessTerminalPart;
import com.github.glodblock.epp.common.EPPItemAndBlock;
import net.minecraft.world.item.Item;

public class ItemPatternAccessTerminalUpgrade extends ItemUpgrade {

    public ItemPatternAccessTerminalUpgrade() {
        super(new Item.Properties());
        this.addPart(PatternAccessTerminalPart.class, EPPItemAndBlock.EX_PATTERN_TERMINAL);
    }

}
