package com.glodblock.github.extendedae.common.blocks;

import appeng.api.orientation.IOrientationStrategy;
import appeng.api.orientation.OrientationStrategies;
import com.glodblock.github.extendedae.common.tileentities.TileExDrive;
import net.minecraft.world.entity.player.Player;

public class BlockExDrive extends BlockBaseGui<TileExDrive> {

    @Override
    public void openGui(TileExDrive tile, Player p) {
        tile.openMenu(p);
    }

    @Override
    public IOrientationStrategy getOrientationStrategy() {
        return OrientationStrategies.full();
    }

}
