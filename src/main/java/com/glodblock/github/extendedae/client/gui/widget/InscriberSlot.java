package com.glodblock.github.extendedae.client.gui.widget;

import appeng.api.inventories.InternalInventory;
import appeng.menu.slot.AppEngSlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class InscriberSlot extends AppEngSlot {

    public InscriberSlot(InternalInventory inv, int invSlot) {
        super(inv, invSlot);
    }

    public @NotNull ItemStack safeInsert(@NotNull ItemStack input, int amount) {
        var stack = this.getItem();
        if (!stack.isEmpty() && stack.getCount() >= this.getMaxStackSize()) {
            return input;
        }
        return super.safeInsert(input, amount);
    }

}
