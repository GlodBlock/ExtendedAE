package com.github.glodblock.epp.common.blocks;

import appeng.menu.locator.MenuLocators;
import com.github.glodblock.epp.common.tileentities.TileExInterface;
import net.minecraft.world.entity.player.Player;

public class BlockExInterface extends BlockBaseGui<TileExInterface> {

    @Override
    public void openGui(TileExInterface tile, Player p) {
        tile.openMenu(p, MenuLocators.forBlockEntity(tile));
    }

}
