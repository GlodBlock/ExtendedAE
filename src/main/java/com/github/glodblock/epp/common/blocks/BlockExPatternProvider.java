package com.github.glodblock.epp.common.blocks;

import appeng.api.util.IOrientable;
import appeng.block.AEBaseEntityBlock;
import appeng.menu.locator.MenuLocators;
import appeng.util.InteractionUtil;
import com.github.glodblock.epp.common.tileentities.TileExPatternProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;

@SuppressWarnings("deprecation")
public class BlockExPatternProvider extends AEBaseEntityBlock<TileExPatternProvider> {

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
    public InteractionResult onActivated(Level level, BlockPos pos, Player p, InteractionHand hand, @Nullable ItemStack heldItem, BlockHitResult hit) {
        if (InteractionUtil.isInAlternateUseMode(p)) {
            return InteractionResult.PASS;
        } else {
            var be = this.getBlockEntity(level, pos);
            if (be != null) {
                if (!level.isClientSide()) {
                    be.openMenu(p, MenuLocators.forBlockEntity(be));
                }
                return InteractionResult.sidedSuccess(level.isClientSide());
            } else {
                return InteractionResult.PASS;
            }
        }
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
