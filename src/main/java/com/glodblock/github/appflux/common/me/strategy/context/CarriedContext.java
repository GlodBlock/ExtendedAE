package com.glodblock.github.appflux.common.me.strategy.context;

import com.glodblock.github.appflux.api.ItemContext;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public record CarriedContext(Player player, AbstractContainerMenu menu) implements ItemContext {

    @Override
    public ItemStack getStack() {
        return this.menu.getCarried();
    }

    @Override
    public void setStack(ItemStack stack) {
        this.menu.setCarried(stack);
    }

    public void addOverflow(ItemStack stack) {
        if (this.menu.getCarried().isEmpty()) {
            this.menu.setCarried(stack);
        } else {
            this.player.getInventory().placeItemBackInInventory(stack);
        }
    }

}
