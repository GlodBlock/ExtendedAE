package com.glodblock.github.appflux.common.items;

import net.minecraft.world.item.ItemStack;

public class ItemPortableMEGAFECell extends ItemPortableFECell {

    public ItemPortableMEGAFECell(int quartKilobytes, double idleDrain, int defaultColor) {
        super(quartKilobytes, idleDrain, defaultColor);
    }

    @Override
    public double getChargeRate(ItemStack stack) {
        return super.getChargeRate(stack) * 2.0;
    }

    @Override
    public double getAEMaxPower(ItemStack stack) {
        return super.getAEMaxPower(stack) * 8.0;
    }

}
