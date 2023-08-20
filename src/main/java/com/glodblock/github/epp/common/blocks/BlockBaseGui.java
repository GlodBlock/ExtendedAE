package com.glodblock.github.epp.common.blocks;

import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.util.InteractionUtil;
import net.minecraft.block.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BlockBaseGui<T extends AEBaseBlockEntity> extends AEBaseEntityBlock<T> {

    public BlockBaseGui(Settings props) {
        super(props);
    }

    public BlockBaseGui() {
        this(defaultProps(Material.METAL));
    }

    @Override
    public ActionResult onActivated(World level, BlockPos pos, PlayerEntity p, Hand hand, ItemStack heldItem, BlockHitResult hit) {
        if (InteractionUtil.isInAlternateUseMode(p)) {
            return ActionResult.PASS;
        } else {
            var te = this.getBlockEntity(level, pos);
            if (te != null) {
                if (!level.isClient()) {
                    this.openGui(te, p);
                }
                return ActionResult.success(level.isClient());
            } else {
                return ActionResult.PASS;
            }
        }
    }

    public abstract void openGui(T tile, PlayerEntity p);

}
