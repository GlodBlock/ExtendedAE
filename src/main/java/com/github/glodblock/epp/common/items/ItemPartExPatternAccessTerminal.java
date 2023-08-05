package com.github.glodblock.epp.common.items;

import appeng.api.parts.IPartItem;
import appeng.api.parts.PartHelper;
import com.github.glodblock.epp.common.EPPItemAndBlock;
import com.github.glodblock.epp.common.parts.PartExPatternAccessTerminal;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

import javax.annotation.Nonnull;

public class ItemPartExPatternAccessTerminal extends Item implements IPartItem<PartExPatternAccessTerminal> {

    public ItemPartExPatternAccessTerminal() {
        super(new Item.Properties());
    }

    @Nonnull
    @Override
    public InteractionResult useOn(@Nonnull UseOnContext context) {
        return PartHelper.usePartItem(context);
    }

    @Override
    public Class<PartExPatternAccessTerminal> getPartClass() {
        return PartExPatternAccessTerminal.class;
    }

    @Override
    public PartExPatternAccessTerminal createPart() {
        return new PartExPatternAccessTerminal(EPPItemAndBlock.EX_PATTERN_TERMINAL);
    }
}
