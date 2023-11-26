package com.github.glodblock.extendedae.common.blocks;

import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import com.github.glodblock.extendedae.common.tileentities.TileExMolecularAssembler;
import com.github.glodblock.extendedae.container.ContainerExMolecularAssembler;
import net.minecraft.world.entity.player.Player;

public class BlockExMolecularAssembler extends BlockBaseGui<TileExMolecularAssembler> {

    public BlockExMolecularAssembler() {
        super(metalProps().noOcclusion());
    }

    @Override
    public void openGui(TileExMolecularAssembler tile, Player p) {
        MenuOpener.open(ContainerExMolecularAssembler.TYPE, p, MenuLocators.forBlockEntity(tile));
    }

}
