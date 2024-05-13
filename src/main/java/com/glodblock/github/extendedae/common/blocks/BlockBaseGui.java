package com.glodblock.github.extendedae.common.blocks;

import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.util.InteractionUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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
    public InteractionResult onActivated(Level level, BlockPos pos, Player p, InteractionHand hand, @Nullable ItemStack heldItem, BlockHitResult hit) {
        if (InteractionUtil.isInAlternateUseMode(p)) {
            return InteractionResult.PASS;
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
                return InteractionResult.sidedSuccess(level.isClientSide());
            } else {
                return InteractionResult.PASS;
            }
        }
    }

    public abstract void openGui(T tile, Player p);

    @Nullable
    public InteractionResult check(T tile, ItemStack stack, Level world, BlockPos pos, BlockHitResult hit, Player p) {
        return null;
    }

}
