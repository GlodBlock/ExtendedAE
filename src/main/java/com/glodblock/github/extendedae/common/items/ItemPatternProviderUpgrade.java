package com.glodblock.github.extendedae.common.items;

import appeng.blockentity.crafting.PatternProviderBlockEntity;
import appeng.parts.crafting.PatternProviderPart;
import com.glodblock.github.extendedae.common.EAEItemAndBlock;
import com.glodblock.github.extendedae.common.tileentities.TileExPatternProvider;
import net.minecraft.world.item.Item;

public class ItemPatternProviderUpgrade extends ItemUpgrade {

    public ItemPatternProviderUpgrade() {
        super(new Item.Properties());
        this.addTile(PatternProviderBlockEntity.class, EAEItemAndBlock.EX_PATTERN_PROVIDER, TileExPatternProvider.class);
        this.addPart(PatternProviderPart.class, EAEItemAndBlock.EX_PATTERN_PROVIDER_PART);
    }

}
