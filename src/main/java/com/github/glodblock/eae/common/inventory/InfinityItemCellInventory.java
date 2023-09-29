package com.github.glodblock.eae.common.inventory;

import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import com.github.glodblock.eae.common.EAEItemAndBlock;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class InfinityItemCellInventory implements IMEInventoryHandler<IAEItemStack> {

    @Nonnull
    private final IAEItemStack record;
    private static final long SIZE = Integer.MAX_VALUE;

    private InfinityItemCellInventory(ItemStack stack) {
        Object obj = EAEItemAndBlock.INFINITY_CELL.getRecord(stack);
        if (obj == null) {
            throw new IllegalArgumentException("Cell isn't an infinity cell!");
        }
        if (obj instanceof IAEItemStack) {
            this.record = (IAEItemStack) obj;
            this.record.setStackSize(SIZE);
        } else {
            throw new IllegalArgumentException("Wrong infinity cell record!");
        }
    }

    public static InfinityItemCellInventory getInventory(ItemStack stack) {
        try {
            return new InfinityItemCellInventory(stack);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public IAEItemStack injectItems(IAEItemStack stack, Actionable actionable, IActionSource iActionSource) {
        if (this.record.equals(stack)) {
            return null;
        }
        return stack;
    }

    @Override
    public IAEItemStack extractItems(IAEItemStack stack, Actionable actionable, IActionSource iActionSource) {
        if (this.record.equals(stack)) {
            return stack.copy();
        }
        return null;
    }

    @Override
    public IItemList<IAEItemStack> getAvailableItems(IItemList<IAEItemStack> iItemList) {
        iItemList.add(this.record);
        return iItemList;
    }

    @Override
    public IStorageChannel<IAEItemStack> getChannel() {
        return AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class);
    }

    @Override
    public AccessRestriction getAccess() {
        return AccessRestriction.READ_WRITE;
    }

    @Override
    public boolean isPrioritized(IAEItemStack stack) {
        return this.record.equals(stack);
    }

    @Override
    public boolean canAccept(IAEItemStack stack) {
        return this.record.equals(stack);
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public int getSlot() {
        return 0;
    }

    @Override
    public boolean validForPass(int i) {
        return true;
    }
}
