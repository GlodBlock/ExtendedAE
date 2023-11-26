package com.github.glodblock.extendedae.client.gui.widget;

import appeng.api.inventories.InternalInventory;
import appeng.menu.slot.FakeSlot;
import net.minecraft.world.item.ItemStack;

public class SingleFakeSlot extends FakeSlot {

    public SingleFakeSlot(InternalInventory inv, int invSlot) {
        super(inv, invSlot);
    }

    @Override
    public void set(ItemStack is) {
        if (!is.isEmpty()) {
            is = is.copy();
            is.setCount(1);
        }
        super.set(is);
    }

}
