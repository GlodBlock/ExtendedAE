package com.github.glodblock.epp.common.items;

import appeng.api.parts.IPartItem;
import appeng.api.parts.PartHelper;
import com.github.glodblock.epp.common.EPPItemAndBlock;
import com.github.glodblock.epp.common.parts.PartExImportBus;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

import javax.annotation.Nonnull;

public class ItemPartExImportBus extends Item implements IPartItem<PartExImportBus> {

    public ItemPartExImportBus() {
        super(new Item.Properties());
    }

    @Nonnull
    @Override
    public InteractionResult useOn(@Nonnull UseOnContext context) {
        return PartHelper.usePartItem(context);
    }

    @Override
    public Class<PartExImportBus> getPartClass() {
        return PartExImportBus.class;
    }

    @Override
    public PartExImportBus createPart() {
        return new PartExImportBus(EPPItemAndBlock.EX_IMPORT_BUS);
    }

}