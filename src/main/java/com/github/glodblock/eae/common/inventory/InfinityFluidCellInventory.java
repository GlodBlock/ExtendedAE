package com.github.glodblock.eae.common.inventory;

import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IFluidStorageChannel;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IItemList;
import com.github.glodblock.eae.common.EAEItemAndBlock;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class InfinityFluidCellInventory implements IMEInventoryHandler<IAEFluidStack> {

    @Nonnull
    private final IAEFluidStack record;
    private static final long SIZE = Integer.MAX_VALUE * 1000L;

    private InfinityFluidCellInventory(ItemStack stack) {
        Object obj = EAEItemAndBlock.INFINITY_CELL.getRecord(stack);
        if (obj == null) {
            throw new IllegalArgumentException("Cell isn't an infinity cell!");
        }
        if (obj instanceof IAEFluidStack) {
            this.record = (IAEFluidStack) obj;
            this.record.setStackSize(SIZE);
        } else {
            throw new IllegalArgumentException("Wrong infinity cell record!");
        }
    }

    public static InfinityFluidCellInventory getInventory(ItemStack stack) {
        try {
            return new InfinityFluidCellInventory(stack);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public IAEFluidStack injectItems(IAEFluidStack stack, Actionable actionable, IActionSource iActionSource) {
        if (this.record.equals(stack)) {
            return null;
        }
        return stack;
    }

    @Override
    public IAEFluidStack extractItems(IAEFluidStack stack, Actionable actionable, IActionSource iActionSource) {
        if (this.record.equals(stack)) {
            return stack;
        }
        return null;
    }

    @Override
    public IItemList<IAEFluidStack> getAvailableItems(IItemList<IAEFluidStack> iItemList) {
        iItemList.add(this.record);
        return iItemList;
    }

    @Override
    public IStorageChannel<IAEFluidStack> getChannel() {
        return AEApi.instance().storage().getStorageChannel(IFluidStorageChannel.class);
    }

    @Override
    public AccessRestriction getAccess() {
        return AccessRestriction.READ_WRITE;
    }

    @Override
    public boolean isPrioritized(IAEFluidStack stack) {
        return this.record.equals(stack);
    }

    @Override
    public boolean canAccept(IAEFluidStack stack) {
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
