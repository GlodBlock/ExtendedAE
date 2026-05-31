package com.glodblock.github.extendedae.common.items;

import appeng.parts.reporting.PatternAccessTerminalPart;
import com.glodblock.github.extendedae.common.EAESingletons;

public class ItemPatternAccessTerminalUpgrade extends ItemUpgrade {

    public ItemPatternAccessTerminalUpgrade(Properties props) {
        super(props);
        this.addPart(PatternAccessTerminalPart.class, EAESingletons.EX_PATTERN_TERMINAL);
    }

}
