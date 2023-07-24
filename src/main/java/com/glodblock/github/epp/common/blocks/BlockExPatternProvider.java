package com.glodblock.github.epp.common.blocks;

import appeng.api.util.IOrientable;
import appeng.block.AEBaseEntityBlock;
import appeng.menu.locator.MenuLocators;
import appeng.util.InteractionUtil;
import com.glodblock.github.epp.common.tiles.TileExPatternProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

@SuppressWarnings("deprecation")
public class BlockExPatternProvider extends AEBaseEntityBlock<TileExPatternProvider> {

    private static final BooleanProperty OMNIDIRECTIONAL = BooleanProperty.of("omnidirectional");
    private static final DirectionProperty FACING = DirectionProperty.of("facing", EnumSet.allOf(Direction.class));

    public BlockExPatternProvider() {
        super(defaultProps(Material.METAL));
        this.setDefaultState(this.getDefaultState().with(OMNIDIRECTIONAL, true));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(OMNIDIRECTIONAL, FACING);
    }

    @Override
    protected BlockState updateBlockStateFromBlockEntity(BlockState currentState, TileExPatternProvider be) {
        return currentState
                .with(OMNIDIRECTIONAL, be.isOmniDirectional())
                .with(FACING, be.getForward());
    }

    @Override
    public void neighborUpdate(@NotNull BlockState state, @NotNull World level, @NotNull BlockPos pos, @NotNull Block block, @NotNull BlockPos fromPos, boolean isMoving) {
        var be = this.getBlockEntity(level, pos);
        if (be != null) {
            be.getLogic().updateRedstoneState();
        }
    }

    @Override
    public ActionResult onActivated(World level, BlockPos pos, PlayerEntity p, Hand hand, ItemStack heldItem, BlockHitResult hit) {
        if (InteractionUtil.isInAlternateUseMode(p)) {
            return ActionResult.PASS;
        } else {
            var be = this.getBlockEntity(level, pos);
            if (be != null) {
                if (!level.isClient()) {
                    be.openMenu(p, MenuLocators.forBlockEntity(be));
                }
                return ActionResult.success(level.isClient());
            } else {
                return ActionResult.PASS;
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
