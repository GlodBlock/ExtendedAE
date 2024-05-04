package com.glodblock.github.ae2netanalyser.common.inventory;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.menu.locator.ItemMenuHostLocator;
import com.glodblock.github.ae2netanalyser.common.items.ItemNetworkAnalyzer;
import net.minecraft.world.entity.player.Player;

public class DummyItemInventory extends ItemMenuHost<ItemNetworkAnalyzer> {

    public DummyItemInventory(ItemNetworkAnalyzer item, Player player, ItemMenuHostLocator locator) {
        super(item, player, locator);
    }

}
