package com.github.glodblock.eae.handler;

import appeng.api.storage.ICellHandler;
import appeng.api.storage.ICellInventoryHandler;
import appeng.api.storage.ISaveProvider;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import appeng.me.storage.BasicCellInventoryHandler;
import com.github.glodblock.eae.common.EAEItemAndBlock;
import com.github.glodblock.eae.common.inventory.InfinityFluidCellInventory;
import com.github.glodblock.eae.common.inventory.InfinityItemCellInventory;
import net.minecraft.item.ItemStack;

public class InfinityCellHandler implements ICellHandler {

    @Override
    public boolean isCell(ItemStack itemStack) {
        return itemStack.getItem() == EAEItemAndBlock.INFINITY_CELL;
    }

    @Override
    public <T extends IAEStack<T>> ICellInventoryHandler<T> getCellInventory(ItemStack itemStack,
                                                                             ISaveProvider saveProvider,
                                                                             IStorageChannel<T> storageChannel) {
        InfinityItemCellInventory itemCell = InfinityItemCellInventory.getInventory(itemStack);
        if (itemCell != null && itemCell.getChannel() == storageChannel) {
            return new BasicCellInventoryHandler<>(itemCell, storageChannel);
        }
        InfinityFluidCellInventory fluidCell = InfinityFluidCellInventory.getInventory(itemStack);
        if (fluidCell != null && fluidCell.getChannel() == storageChannel) {
            return new BasicCellInventoryHandler<>(fluidCell, storageChannel);
        }
        return null;
    }

    @Override
    public int getStatusForCell(ItemStack is, ICellInventoryHandler handler) {
        return 2;
    }

    @Override
    public double cellIdleDrain(ItemStack is, ICellInventoryHandler handler) {
        return 8.0;
    }

}
