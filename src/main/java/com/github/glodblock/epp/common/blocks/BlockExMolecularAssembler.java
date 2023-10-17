package com.github.glodblock.epp.common.blocks;

import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import com.github.glodblock.epp.common.tileentities.TileExMolecularAssembler;
import com.github.glodblock.epp.container.ContainerExMolecularAssembler;
import net.minecraft.world.entity.player.Player;

public class BlockExMolecularAssembler extends BlockBaseGui<TileExMolecularAssembler> {

    @Override
    public void openGui(TileExMolecularAssembler tile, Player p) {
        MenuOpener.open(ContainerExMolecularAssembler.TYPE, p, MenuLocators.forBlockEntity(tile));
    }

}
