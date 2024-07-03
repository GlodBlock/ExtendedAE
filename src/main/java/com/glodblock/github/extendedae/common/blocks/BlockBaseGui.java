package com.glodblock.github.extendedae.common.blocks;

import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.util.InteractionUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public abstract class BlockBaseGui<T extends AEBaseBlockEntity> extends AEBaseEntityBlock<T> {

    public BlockBaseGui(Properties props) {
        super(props);
    }

    public BlockBaseGui() {
        super(metalProps());
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player p, BlockHitResult hit) {
        var be = this.getBlockEntity(level, pos);
        if (be != null) {
            if (!level.isClientSide()) {
                this.openGui(be, p);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        } else {
            return InteractionResult.PASS;
        }
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack heldItem, BlockState state, Level level, BlockPos pos, Player p, InteractionHand hand, BlockHitResult hit) {
        var parent = super.useItemOn(heldItem, state, level, pos, p, hand, hit);
        if (parent.result() != InteractionResult.PASS) {
            return parent;
        }
        if (InteractionUtil.isInAlternateUseMode(p)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        } else {
            var be = this.getBlockEntity(level, pos);
            if (be != null) {
                var ir = check(be, heldItem, level, pos, hit, p);
                if (ir != null) {
                    return ir;
                }
                if (!level.isClientSide()) {
                    this.openGui(be, p);
                }
                return ItemInteractionResult.SUCCESS;
            } else {
                return ItemInteractionResult.FAIL;
            }
        }
    }

    public abstract void openGui(T tile, Player p);

    @Nullable
    public ItemInteractionResult check(T tile, ItemStack stack, Level world, BlockPos pos, BlockHitResult hit, Player p) {
        return null;
    }

}
