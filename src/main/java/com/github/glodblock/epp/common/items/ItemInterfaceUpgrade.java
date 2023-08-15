package com.github.glodblock.epp.common.items;

import appeng.blockentity.misc.InterfaceBlockEntity;
import appeng.parts.misc.InterfacePart;
import com.github.glodblock.epp.common.EPPItemAndBlock;
import com.github.glodblock.epp.common.tileentities.TileExInterface;
import net.minecraft.world.item.Item;

public class ItemInterfaceUpgrade extends ItemUpgrade {

    public ItemInterfaceUpgrade() {
        super(new Item.Properties());
        this.addTile(InterfaceBlockEntity.class, EPPItemAndBlock.EX_INTERFACE, TileExInterface.class);
        this.addPart(InterfacePart.class, EPPItemAndBlock.EX_INTERFACE_PART);
    }

}
