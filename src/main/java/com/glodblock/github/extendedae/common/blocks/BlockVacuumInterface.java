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
import net.minecraft.world.level.redstone.Orientation;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public class BlockVacuumInterface extends BlockBaseGui<TileVacuumInterface> {

    public BlockVacuumInterface(Properties props) {
        super(props);
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Block block, @Nullable Orientation orientation, boolean movedByPiston) {
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
