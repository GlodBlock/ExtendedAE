package com.glodblock.github.appflux.common.blocks;

import appeng.block.AEBaseEntityBlock;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import appeng.util.InteractionUtil;
import com.glodblock.github.appflux.common.tileentities.TileFluxAccessor;
import com.glodblock.github.appflux.container.ContainerFluxAccessor;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockFluxAccessor extends AEBaseEntityBlock<TileFluxAccessor> {

    public BlockFluxAccessor() {
        super(metalProps());
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Item.TooltipContext ctx, List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable("block.appflux.flux_accessor.tooltip.1").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("block.appflux.flux_accessor.tooltip.2").withStyle(ChatFormatting.GRAY));
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
                if (!level.isClientSide()) {
                    MenuOpener.open(ContainerFluxAccessor.TYPE, p, MenuLocators.forBlockEntity(be));
                }
                return ItemInteractionResult.SUCCESS;
            } else {
                return ItemInteractionResult.FAIL;
            }
        }
    }

}
