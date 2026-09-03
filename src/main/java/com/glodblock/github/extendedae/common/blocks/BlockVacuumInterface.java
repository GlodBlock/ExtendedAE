package com.glodblock.github.extendedae.common.blocks;

import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import com.glodblock.github.extendedae.common.tileentities.TileVacuumInterface;
import com.glodblock.github.extendedae.container.ContainerVacuumInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockVacuumInterface extends BlockBaseGui<TileVacuumInterface> {

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Block block, @NotNull BlockPos fromPos, boolean isMoving) {
        var be = this.getBlockEntity(level, pos);
        if (be != null) {
            be.updateRedstoneState();
            be.changeWorkStatus();
            be.updateSleepness();
        }
    }

    @Override
    public void openGui(TileVacuumInterface tile, Player p) {
        MenuOpener.open(ContainerVacuumInterface.TYPE, p, MenuLocators.forBlockEntity(tile));
    }

}
