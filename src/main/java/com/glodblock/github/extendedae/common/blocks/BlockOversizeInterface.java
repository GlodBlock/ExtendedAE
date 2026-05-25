package com.glodblock.github.extendedae.common.blocks;

import appeng.menu.locator.MenuLocators;
import com.glodblock.github.extendedae.common.tileentities.TileOversizeInterface;
import net.minecraft.world.entity.player.Player;

public class BlockOversizeInterface extends BlockBaseGui<TileOversizeInterface> {

    public BlockOversizeInterface(Properties props) {
        super(props);
    }

    @Override
    public void openGui(TileOversizeInterface tile, Player p) {
        tile.openMenu(p, MenuLocators.forBlockEntity(tile));
    }

}
