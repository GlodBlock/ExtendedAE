package com.glodblock.github.ae2netanalyser.common.inventory;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.menu.locator.ItemMenuHostLocator;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

public class DummyItemInventory extends ItemMenuHost<Item> {

    public DummyItemInventory(Item item, Player player, ItemMenuHostLocator locator) {
        super(item, player, locator);
    }

}
