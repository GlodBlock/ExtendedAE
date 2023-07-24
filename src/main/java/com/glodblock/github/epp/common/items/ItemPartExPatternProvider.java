package com.glodblock.github.epp.common.items;

import appeng.api.parts.IPartItem;
import appeng.api.parts.PartHelper;
import com.glodblock.github.epp.common.EPPItemAndBlock;
import com.glodblock.github.epp.common.parts.PartExPatternProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;

public class ItemPartExPatternProvider extends Item implements IPartItem<PartExPatternProvider> {

    public ItemPartExPatternProvider() {
        super(new Item.Settings().group(EPPItemAndBlock.TAB));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        return PartHelper.usePartItem(context);
    }

    @Override
    public Class<PartExPatternProvider> getPartClass() {
        return PartExPatternProvider.class;
    }

    @Override
    public PartExPatternProvider createPart() {
        return new PartExPatternProvider(EPPItemAndBlock.EX_PATTERN_PROVIDER_PART);
    }
}
