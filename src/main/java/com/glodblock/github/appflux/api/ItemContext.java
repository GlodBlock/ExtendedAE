package com.glodblock.github.appflux.api;

import net.minecraft.world.item.ItemStack;

public interface ItemContext {

    ItemStack getStack();

    void setStack(ItemStack stack);

    void addOverflow(ItemStack stack);

}
