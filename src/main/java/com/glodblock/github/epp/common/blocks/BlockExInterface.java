package com.glodblock.github.epp.common.blocks;

import appeng.block.AEBaseEntityBlock;
import appeng.menu.locator.MenuLocators;
import appeng.util.InteractionUtil;
import com.glodblock.github.epp.common.tiles.TileExInterface;
import net.minecraft.block.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockExInterface extends AEBaseEntityBlock<TileExInterface> {

    public BlockExInterface() {
        super(defaultProps(Material.METAL));
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

}
