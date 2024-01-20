package com.glodblock.github.appflux.common.me.strategy.context;

import com.glodblock.github.appflux.api.ItemContext;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public record PlayerInvContext(Player player, int slot) implements ItemContext {

    @Override
    public ItemStack getStack() {
        return this.player.getInventory().getItem(this.slot);
    }

    @Override
    public void setStack(ItemStack stack) {
        this.player.getInventory().setItem(this.slot, stack);
    }

    public void addOverflow(ItemStack stack) {
        this.player.getInventory().placeItemBackInInventory(stack);
    }

}
