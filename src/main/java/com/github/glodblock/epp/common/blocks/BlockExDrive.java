package com.github.glodblock.epp.common.blocks;

import com.github.glodblock.epp.common.tileentities.TileExDrive;
import net.minecraft.world.entity.player.Player;

public class BlockExDrive extends BlockBaseGui<TileExDrive> {

    @Override
    public void openGui(TileExDrive tile, Player p) {
        tile.openMenu(p);
    }

}
