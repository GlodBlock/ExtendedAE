package com.github.glodblock.epp.common.items;

import appeng.api.parts.IPartItem;
import appeng.api.parts.PartHelper;
import com.github.glodblock.epp.common.EPPItemAndBlock;
import com.github.glodblock.epp.common.parts.PartExInterface;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

import javax.annotation.Nonnull;

public class ItemPartExInterface extends Item implements IPartItem<PartExInterface> {

    public ItemPartExInterface() {
        super(new Item.Properties());
    }

    @Nonnull
    @Override
    public InteractionResult useOn(@Nonnull UseOnContext context) {
        return PartHelper.usePartItem(context);
    }

    @Override
    public Class<PartExInterface> getPartClass() {
        return PartExInterface.class;
    }

    @Override
    public PartExInterface createPart() {
        return new PartExInterface(EPPItemAndBlock.EX_INTERFACE_PART);
    }

}
