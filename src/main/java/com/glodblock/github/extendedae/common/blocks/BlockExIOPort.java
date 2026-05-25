package com.glodblock.github.extendedae.common.blocks;

import appeng.api.orientation.IOrientationStrategy;
import appeng.api.orientation.OrientationStrategies;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import com.glodblock.github.extendedae.common.tileentities.TileExIOPort;
import com.glodblock.github.extendedae.container.ContainerExIOPort;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.Orientation;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public class BlockExIOPort extends BlockBaseGui<TileExIOPort> {

    public final static BooleanProperty POWERED = BooleanProperty.create("powered");

    public BlockExIOPort(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(POWERED, false));
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Block block, @Nullable Orientation orientation, boolean movedByPiston) {
        final var te = this.getBlockEntity(level, pos);
        if (te != null) {
            te.updateRedstoneState();
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<@NotNull Block, @NotNull BlockState> builder) {
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
