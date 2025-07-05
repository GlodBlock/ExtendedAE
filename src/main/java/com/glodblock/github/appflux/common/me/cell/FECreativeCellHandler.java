package com.glodblock.github.appflux.common.me.cell;

import appeng.api.storage.cells.ICellHandler;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;
import com.glodblock.github.appflux.common.items.ItemCreativeFECell;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class FECreativeCellHandler implements ICellHandler {

    public static final FECreativeCellHandler HANDLER = new FECreativeCellHandler();

    @Override
    public boolean isCell(ItemStack is) {
        return !is.isEmpty() && is.getItem() instanceof ItemCreativeFECell;
    }

    @Override
    public @Nullable StorageCell getCellInventory(ItemStack is, @Nullable ISaveProvider host) {
        if (this.isCell(is)) {
            return new FECreativeCellInventory(is);
        }
        return null;
    }

}
