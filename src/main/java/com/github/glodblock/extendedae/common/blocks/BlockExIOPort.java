package com.github.glodblock.extendedae.common.blocks;

import appeng.api.orientation.IOrientationStrategy;
import appeng.api.orientation.OrientationStrategies;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import com.github.glodblock.extendedae.common.tileentities.TileExIOPort;
import com.github.glodblock.extendedae.container.ContainerExIOPort;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.NotNull;

public class BlockExIOPort extends BlockBaseGui<TileExIOPort> {

    public final static BooleanProperty POWERED = BooleanProperty.create("powered");

    public BlockExIOPort() {
        this.registerDefaultState(this.defaultBlockState().setValue(POWERED, false));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Block blockIn, @NotNull BlockPos fromPos, boolean isMoving) {
        final var te = this.getBlockEntity(level, pos);
        if (te != null) {
            te.updateRedstoneState();
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWERED);
    }

    @Override
    public IOrientationStrategy getOrientationStrategy() {
        return OrientationStrategies.full();
    }

    @Override
    protected BlockState updateBlockStateFromBlockEntity(BlockState currentState, TileExIOPort be) {
        return currentState.setValue(POWERED, be.isActive());
    }

    @Override
    public void openGui(TileExIOPort tile, Player p) {
        MenuOpener.open(ContainerExIOPort.TYPE, p, MenuLocators.forBlockEntity(tile));
    }

}
