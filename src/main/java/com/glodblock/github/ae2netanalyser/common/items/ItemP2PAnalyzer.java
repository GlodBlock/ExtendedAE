package com.glodblock.github.ae2netanalyser.common.items;

import appeng.api.implementations.menuobjects.IMenuItem;
import appeng.api.implementations.menuobjects.ItemMenuHost;
import com.glodblock.github.ae2netanalyser.common.inventory.DummyItemInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ItemP2PAnalyzer extends Item implements IMenuItem {

    public ItemP2PAnalyzer() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public @Nullable ItemMenuHost getMenuHost(Player player, int inventorySlot, ItemStack stack, @Nullable BlockPos pos) {
        return new DummyItemInventory(player, inventorySlot, stack);
    }
}
