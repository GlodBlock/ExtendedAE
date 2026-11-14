package com.glodblock.github.extendedae.common.blocks;

import appeng.api.orientation.IOrientationStrategy;
import appeng.api.orientation.OrientationStrategies;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import com.glodblock.github.extendedae.common.tileentities.TilePulverizer;
import com.glodblock.github.extendedae.container.ContainerPulverizer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.NotNull;

public class BlockPulverizer extends BlockBaseGui<TilePulverizer> {

    public final static BooleanProperty WORKING = BooleanProperty.create("working");

    public BlockPulverizer(Properties props) {
        super(props);
        this.registerDefaultState(this.defaultBlockState().setValue(WORKING, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<@NotNull Block, @NotNull BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WORKING);
    }

    @Override
    public IOrientationStrategy getOrientationStrategy() {
        return OrientationStrategies.full();
    }

    @Override
    protected BlockState updateBlockStateFromBlockEntity(BlockState currentState, TilePulverizer be) {
        return currentState.setValue(WORKING, be.isWorking());
    }

    @Override
    public void openGui(TilePulverizer tile, Player p) {
        MenuOpener.open(ContainerPulverizer.TYPE, p, MenuLocators.forBlockEntity(tile));
    }

}
