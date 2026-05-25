package com.glodblock.github.extendedae.common.items;

import appeng.util.InteractionUtil;
import com.glodblock.github.extendedae.common.EAESingletons;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemWirelessConnectTool extends Item {

    public ItemWirelessConnectTool() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext ctx, @NotNull List<Component> lines, @NotNull TooltipFlag adv){
        var locator = stack.get(EAESingletons.WIRELESS_LOCATOR);
        if (locator != null) {
            var freq = locator.left();
            var globalPos = locator.right();
            BlockPos pos = globalPos != null ? globalPos.pos() : BlockPos.ZERO;
            if (freq != 0) {
                lines.add(Component.translatable("wireless.tooltip", pos.getX(), pos.getY(), pos.getZ()).withStyle(ChatFormatting.GRAY));
                lines.add(Component.translatable("wireless.use.tooltip.02").withStyle(ChatFormatting.GRAY));
            } else {
                lines.add(Component.translatable("wireless.use.tooltip.01").withStyle(ChatFormatting.GRAY));
            }
        } else {
            lines.add(Component.translatable("wireless.use.tooltip.01").withStyle(ChatFormatting.GRAY));
        }
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        var stack = player.getItemInHand(hand);
        if (InteractionUtil.isInAlternateUseMode(player) && stack.getItem() == EAESingletons.WIRELESS_TOOL) {
            stack.remove(EAESingletons.WIRELESS_LOCATOR);
            player.displayClientMessage(Component.translatable("chat.wireless_connect.clear"), true);
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }
        return InteractionResultHolder.pass(stack);
    }

}
