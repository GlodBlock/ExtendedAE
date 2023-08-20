package com.glodblock.github.epp.common.blocks;

import appeng.menu.locator.MenuLocators;
import com.glodblock.github.epp.common.tiles.TileExInterface;
import net.minecraft.entity.player.PlayerEntity;

public class BlockExInterface extends BlockBaseGui<TileExInterface> {

    @Override
    public void openGui(TileExInterface tile, PlayerEntity p) {
        tile.openMenu(p, MenuLocators.forBlockEntity(tile));
    }

}
