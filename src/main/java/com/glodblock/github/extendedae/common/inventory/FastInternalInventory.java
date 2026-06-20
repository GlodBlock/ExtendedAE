package com.glodblock.github.extendedae.common.inventory;

import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.InternalInventoryHost;
import appeng.util.inv.filter.IAEItemFilter;
import com.glodblock.github.extendedae.common.handler.FastResourceHandler;
import com.glodblock.github.extendedae.util.helper.IntruderInventory;
import com.google.common.base.Preconditions;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FastInternalInventory extends AppEngInternalInventory {

    private final IntruderInventory internal;

    public FastInternalInventory(InternalInventoryHost host, int size, int maxStack, IAEItemFilter filter) {
        super(host, size, maxStack, filter);
        this.internal = (IntruderInventory) this;
    }

    public FastInternalInventory(@Nullable InternalInventoryHost inventory, int size, int maxStack) {
        this(inventory, size, maxStack, null);
    }

    public FastInternalInventory(int size) {
        this(null, size, 64);
    }

    public FastInternalInventory(@Nullable InternalInventoryHost inventory, int size) {
        this(inventory, size, 64);
    }

    public void setItemSilent(int slot, ItemStack stack) {
        this.internal.setStackSilent(slot, stack);
    }

    public ItemStack addItemsSilent(ItemStack stack) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        if (this.size() <= 54) {
            return this.addItemSlowSilent(stack);
        } else {
            return this.addItemFastSilent(stack);
        }
    }

    private ItemStack addItemSlowSilent(ItemStack stack) {
        var remainder = stack.copy();

        for (int pass = 0; pass < 2; pass++) {
            boolean fillEmptySlots = pass == 1;

            for (int slot = 0; slot < size(); slot++) {
                if (getStackInSlot(slot).isEmpty() == fillEmptySlots) {
                    remainder = insertItemSilent(slot, remainder);
                }
                if (remainder.isEmpty()) {
                    return ItemStack.EMPTY;
                }
            }
        }

        return remainder;
    }

    private ItemStack addItemFastSilent(ItemStack stack) {
        var remainder = stack.copy();

        for (int slot = 0; slot < size(); slot++) {
            remainder = insertItemSilent(slot, remainder);
            if (remainder.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }

        return remainder;
    }

    public ItemStack insertItemSilent(int slot, ItemStack stack) {
        Preconditions.checkArgument(slot >= 0 && slot < size(), "slot out of range");
        if (stack.isEmpty() || !isItemValid(slot, stack)) {
            return stack;
        }
        var inSlot = getStackInSlot(slot);
        int maxSpace = Math.min(getSlotLimit(slot), stack.getMaxStackSize());
        int freeSpace = maxSpace - inSlot.getCount();
        if (freeSpace <= 0) {
            return stack;
        }
        if (!inSlot.isEmpty() && !ItemStack.isSameItemSameComponents(inSlot, stack)) {
            return stack;
        }
        var insertAmount = Math.min(stack.getCount(), freeSpace);
        var newItem = inSlot.isEmpty() ? stack.copy() : inSlot.copy();
        newItem.setCount(inSlot.getCount() + insertAmount);
        setItemSilent(slot, newItem);
        if (freeSpace >= stack.getCount()) {
            return ItemStack.EMPTY;
        } else {
            var r = stack.copy();
            r.shrink(insertAmount);
            return r;
        }
    }

    public ItemStack extractItemsSilent(int amount, ItemStack filter) {
        int slots = size();
        ItemStack rv = ItemStack.EMPTY;
        for (int slot = 0; slot < slots && amount > 0; slot++) {
            final ItemStack is = getStackInSlot(slot);
            if (is.isEmpty() || !filter.isEmpty() && !ItemStack.isSameItemSameComponents(is, filter)) {
                continue;
            }
            ItemStack extracted = extractItemSilent(slot, amount);
            if (extracted.isEmpty()) {
                continue;
            }
            if (rv.isEmpty()) {
                rv = extracted;
                filter = extracted;
            } else {
                rv.grow(extracted.getCount());
            }
            amount -= extracted.getCount();
        }
        return rv;
    }

    public ItemStack extractItemSilent(int slot, int amount) {
        var item = getStackInSlot(slot);
        if (item.isEmpty()) {
            return ItemStack.EMPTY;
        }
        if (amount >= item.getCount()) {
            setItemSilent(slot, ItemStack.EMPTY);
            return item;
        } else {
            var result = item.copy();
            result.setCount(amount);
            var reduced = item.copy();
            reduced.shrink(amount);
            setItemDirect(slot, reduced);
            return result;
        }
    }

    @Override
    public void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
    }

    @Override
    protected ResourceHandler<@NotNull ItemResource> createResourceHandler() {
        return new FastResourceHandler(this);
    }

}
