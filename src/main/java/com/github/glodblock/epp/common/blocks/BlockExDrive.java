package com.github.glodblock.epp.common.blocks;

import appeng.api.orientation.IOrientationStrategy;
import appeng.api.orientation.OrientationStrategies;
import com.github.glodblock.epp.common.tileentities.TileExDrive;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

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
