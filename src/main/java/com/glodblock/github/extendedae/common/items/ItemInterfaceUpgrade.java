package com.glodblock.github.extendedae.common.items;

import appeng.blockentity.misc.InterfaceBlockEntity;
import appeng.parts.misc.InterfacePart;
import com.glodblock.github.extendedae.common.EAESingletons;

public class ItemInterfaceUpgrade extends ItemUpgrade {

    public ItemInterfaceUpgrade(Properties properties) {
        super(properties);
        this.addTile(InterfaceBlockEntity.class, EAESingletons.EX_INTERFACE);
        this.addPart(InterfacePart.class, EAESingletons.EX_INTERFACE_PART);
    }

}
