package com.glodblock.github.epp.common.blocks;

import com.glodblock.github.epp.common.tiles.TileWirelessConnector;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockWirelessConnector extends BlockBaseGui<TileWirelessConnector> {

    private static final BooleanProperty CONNECTED = BooleanProperty.of("connected");

    public BlockWirelessConnector() {
        super();
        this.setDefaultState(this.getDefaultState().with(CONNECTED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(CONNECTED);
    }

    @Override
    protected BlockState updateBlockStateFromBlockEntity(BlockState currentState, TileWirelessConnector be) {
        return currentState.with(CONNECTED, be.isConnected());
    }

    @Override
    public void onStateReplaced(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (newState.getBlock() != state.getBlock()) {
            var te = this.getBlockEntity(level, pos);
            if (te != null) {
                te.breakOnRemove();
            }
            super.onStateReplaced(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public void openGui(TileWirelessConnector tile, PlayerEntity p) {

    }

}
