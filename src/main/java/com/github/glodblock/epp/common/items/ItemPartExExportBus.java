package com.github.glodblock.epp.common.items;

import appeng.api.parts.IPartItem;
import appeng.api.parts.PartHelper;
import com.github.glodblock.epp.common.EPPItemAndBlock;
import com.github.glodblock.epp.common.parts.PartExExportBus;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

import javax.annotation.Nonnull;

public class ItemPartExExportBus extends Item implements IPartItem<PartExExportBus> {

    public ItemPartExExportBus() {
        super(new Item.Properties());
    }

    @Nonnull
    @Override
    public InteractionResult useOn(@Nonnull UseOnContext context) {
        return PartHelper.usePartItem(context);
    }

    @Override
    public Class<PartExExportBus> getPartClass() {
        return PartExExportBus.class;
    }

    @Override
    public PartExExportBus createPart() {
        return new PartExExportBus(EPPItemAndBlock.EX_EXPORT_BUS);
    }

}

