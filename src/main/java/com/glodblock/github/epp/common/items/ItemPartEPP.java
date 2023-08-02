package com.glodblock.github.epp.common.items;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.api.parts.PartHelper;
import com.glodblock.github.epp.common.EPPItemAndBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;

import java.util.function.Function;

public class ItemPartEPP<P extends IPart> extends Item implements IPartItem<P> {

    private final Function<IPartItem<P>, P> factory;
    private final Class<P> part;

    public ItemPartEPP(Function<IPartItem<P>, P> factory, Class<P> clazz) {
        super(new Item.Settings().group(EPPItemAndBlock.TAB));
        this.factory = factory;
        this.part = clazz;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        return PartHelper.usePartItem(context);
    }

    @Override
    public Class<P> getPartClass() {
        return this.part;
    }

    @Override
    public P createPart() {
        return this.factory.apply(this);
    }
}
