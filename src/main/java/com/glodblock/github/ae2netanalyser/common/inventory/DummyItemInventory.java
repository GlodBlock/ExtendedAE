package com.glodblock.github.ae2netanalyser.common.inventory;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class DummyItemInventory extends ItemMenuHost {

    public DummyItemInventory(Player player, @Nullable Integer slot, ItemStack itemStack) {
        super(player, slot, itemStack);
    }

}
