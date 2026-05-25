package com.glodblock.github.extendedae.common.blocks.matrix;

import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import com.glodblock.github.extendedae.common.blocks.BlockBaseGui;
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixBase;
import com.glodblock.github.extendedae.container.ContainerAssemblerMatrix;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public abstract class BlockAssemblerMatrixBase<M extends TileAssemblerMatrixBase> extends BlockBaseGui<M> {

    public static final BooleanProperty FORMED = BooleanProperty.create("formed");
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");

    public BlockAssemblerMatrixBase(Properties props) {
        super(props);
        this.registerDefaultState(defaultBlockState().setValue(FORMED, false).setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<@NotNull Block, @NotNull BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWERED);
        builder.add(FORMED);
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState state, LevelReader level, @NotNull ScheduledTickAccess scheduledTickAccess, @NotNull BlockPos pos, @NotNull Direction direction, @NotNull BlockPos neighborPos, @NotNull BlockState neighborState, @NotNull RandomSource random) {
        var te = level.getBlockEntity(pos);
        if (te != null) {
            te.requestModelDataUpdate();
        }
        var mx = this.getBlockEntity(level, pos);
        if (mx != null) {
            mx.updateMultiBlock(neighborPos);
        }
        return super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    public void openGui(M tile, Player p) {
        if (tile.isActive() && tile.isFormed()) {
            MenuOpener.open(ContainerAssemblerMatrix.TYPE, p, MenuLocators.forBlockEntity(tile));
        }
    }

    @Override
    public @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        var tile = this.getBlockEntity(level, pos);
        if (tile != null && !tile.isFormed()) {
            return InteractionResult.PASS;
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    public InteractionResult check(M tile, ItemStack stack, Level world, BlockPos pos, BlockHitResult hit, Player p) {
        if (!(tile.isActive() || tile.isFormed())) {
            return InteractionResult.PASS;
        }
        return null;
    }

    public abstract Item getPresentItem();

}