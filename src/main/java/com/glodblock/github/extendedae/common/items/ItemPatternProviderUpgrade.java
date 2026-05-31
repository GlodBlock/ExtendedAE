package com.glodblock.github.extendedae.common.items;

import appeng.blockentity.crafting.PatternProviderBlockEntity;
import appeng.parts.crafting.PatternProviderPart;
import com.glodblock.github.extendedae.common.EAESingletons;

public class ItemPatternProviderUpgrade extends ItemUpgrade {

    public ItemPatternProviderUpgrade(Properties props) {
        super(props);
        this.addTile(PatternProviderBlockEntity.class, EAESingletons.EX_PATTERN_PROVIDER);
        this.addPart(PatternProviderPart.class, EAESingletons.EX_PATTERN_PROVIDER_PART);
    }

}
