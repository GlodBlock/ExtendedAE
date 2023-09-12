package com.github.glodblock.epp.common.blocks;

import appeng.api.util.IOrientable;
import appeng.menu.locator.MenuLocators;
import com.github.glodblock.epp.common.tileentities.TileExPatternProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.EnumSet;

@SuppressWarnings("deprecation")
public class BlockExPatternProvider extends BlockBaseGui<TileExPatternProvider> {

    private static final BooleanProperty OMNIDIRECTIONAL = BooleanProperty.create("omnidirectional");
    private static final DirectionProperty FACING = DirectionProperty.create("facing", EnumSet.allOf(Direction.class));

    public BlockExPatternProvider() {
        super(defaultProps(Material.METAL));
        this.registerDefaultState(this.defaultBlockState().setValue(OMNIDIRECTIONAL, true));
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(OMNIDIRECTIONAL, FACING);
    }

    @Override
    protected BlockState updateBlockStateFromBlockEntity(BlockState currentState, TileExPatternProvider be) {
        return currentState
                .setValue(OMNIDIRECTIONAL, be.isOmniDirectional())
                .setValue(FACING, be.getForward());
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Block block, @NotNull BlockPos fromPos, boolean isMoving) {
        var be = this.getBlockEntity(level, pos);
        if (be != null) {
            be.getLogic().updateRedstoneState();
        }
    }

    @Override
    public void openGui(TileExPatternProvider tile, Player p) {
        tile.openMenu(p, MenuLocators.forBlockEntity(tile));
    }

    @Override
    protected boolean hasCustomRotation() {
        return true;
    }

    @Override
    protected void customRotateBlock(IOrientable rotatable, Direction axis) {
        if (rotatable instanceof TileExPatternProvider patternProvider) {
            patternProvider.setSide(axis);
        }
    }

}
