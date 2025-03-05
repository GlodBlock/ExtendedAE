package com.glodblock.github.appflux.common.items;

import com.glodblock.github.appflux.common.AFItemAndBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class ItemMEGAFECell extends ItemFECell {

    public ItemMEGAFECell(ItemLike coreItem, int kilobytes, double idleDrain) {
        super(coreItem, kilobytes, idleDrain);
    }

    @Override
    public ItemStack getHousing() {
        return new ItemStack(AFItemAndBlock.MEGA_FE_HOUSING);
    }

}
