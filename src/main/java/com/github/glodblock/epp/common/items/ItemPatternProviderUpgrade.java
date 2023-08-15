package com.github.glodblock.epp.common.items;

import appeng.blockentity.crafting.PatternProviderBlockEntity;
import appeng.parts.crafting.PatternProviderPart;
import com.github.glodblock.epp.common.EPPItemAndBlock;
import com.github.glodblock.epp.common.tileentities.TileExPatternProvider;
import net.minecraft.world.item.Item;

public class ItemPatternProviderUpgrade extends ItemUpgrade {

    public ItemPatternProviderUpgrade() {
        super(new Item.Properties());
        this.addTile(PatternProviderBlockEntity.class, EPPItemAndBlock.EX_PATTERN_PROVIDER, TileExPatternProvider.class);
        this.addPart(PatternProviderPart.class, EPPItemAndBlock.EX_PATTERN_PROVIDER_PART);
    }

}
