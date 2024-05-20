package com.glodblock.github.extendedae.common.blocks;

import appeng.api.orientation.IOrientationStrategy;
import appeng.api.orientation.OrientationStrategies;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import com.glodblock.github.extendedae.common.tileentities.TileCrystalAssembler;
import com.glodblock.github.extendedae.container.ContainerCrystalAssembler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class BlockCrystalAssembler extends BlockBaseGui<TileCrystalAssembler> {

    public final static BooleanProperty WORKING = BooleanProperty.create("working");

    public BlockCrystalAssembler() {
        this.registerDefaultState(this.defaultBlockState().setValue(WORKING, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WORKING);
    }

    @Override
    public IOrientationStrategy getOrientationStrategy() {
        return OrientationStrategies.full();
    }

    @Override
    protected BlockState updateBlockStateFromBlockEntity(BlockState currentState, TileCrystalAssembler be) {
        return currentState.setValue(WORKING, be.isWorking());
    }

    @Override
    public void openGui(TileCrystalAssembler tile, Player p) {
        MenuOpener.open(ContainerCrystalAssembler.TYPE, p, MenuLocators.forBlockEntity(tile));
    }

}
