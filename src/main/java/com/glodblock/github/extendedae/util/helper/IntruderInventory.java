package com.glodblock.github.extendedae.util.helper;

import net.minecraft.world.item.ItemStack;

public interface IntruderInventory {

    ItemStack[] getStacks();

    void setStacks(ItemStack[] stacks);

    void silentClear();

}
