package com.glodblock.github.extendedae.common.blocks;

import appeng.block.crafting.PushDirection;
import appeng.menu.locator.MenuLocators;
import appeng.util.InteractionUtil;
import appeng.util.Platform;
import com.glodblock.github.extendedae.common.tileentities.TileExPatternProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

import static appeng.block.crafting.PatternProviderBlock.PUSH_DIRECTION;

public class BlockExPatternProvider extends BlockBaseGui<TileExPatternProvider> {

    public BlockExPatternProvider() {
        super(metalProps());
        this.registerDefaultState(this.defaultBlockState().setValue(PUSH_DIRECTION, PushDirection.ALL));
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PUSH_DIRECTION);
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Block block, @NotNull BlockPos fromPos, boolean isMoving) {
        var be = this.getBlockEntity(level, pos);
        if (be != null) {
            be.getLogic().updateRedstoneState();
        }
    }

    @Override
    public ItemInteractionResult check(TileExPatternProvider tile, ItemStack stack, Level world, BlockPos pos, BlockHitResult hit, Player p) {
        if (stack != null && InteractionUtil.canWrenchRotate(stack)) {
            this.setSide(world, pos, hit.getDirection());
            return ItemInteractionResult.sidedSuccess(world.isClientSide);
        }
        return null;
    }

    @Override
    public void openGui(TileExPatternProvider tile, Player p) {
        tile.openMenu(p, MenuLocators.forBlockEntity(tile));
    }

    public void setSide(Level level, BlockPos pos, Direction facing) {
        var currentState = level.getBlockState(pos);
        var pushSide = currentState.getValue(PUSH_DIRECTION).getDirection();

        PushDirection newPushDirection;
        if (pushSide == facing.getOpposite()) {
            newPushDirection = PushDirection.fromDirection(facing);
        } else if (pushSide == facing) {
            newPushDirection = PushDirection.ALL;
        } else if (pushSide == null) {
            newPushDirection = PushDirection.fromDirection(facing.getOpposite());
        } else {
            newPushDirection = PushDirection.fromDirection(Platform.rotateAround(pushSide, facing));
        }

        level.setBlockAndUpdate(pos, currentState.setValue(PUSH_DIRECTION, newPushDirection));
    }

}
