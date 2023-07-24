package com.github.glodblock.epp.common.items;

import appeng.api.parts.IPartItem;
import appeng.api.parts.PartHelper;
import com.github.glodblock.epp.common.EPPItemAndBlock;
import com.github.glodblock.epp.common.parts.PartExPatternProvider;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

import javax.annotation.Nonnull;

public class ItemPartExPatternProvider extends Item implements IPartItem<PartExPatternProvider> {

    public ItemPartExPatternProvider() {
        super(new Item.Properties());
    }

    @Nonnull
    @Override
    public InteractionResult useOn(@Nonnull UseOnContext context) {
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
