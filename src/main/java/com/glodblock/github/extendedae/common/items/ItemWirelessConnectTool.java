package com.glodblock.github.extendedae.common.items;

import appeng.util.InteractionUtil;
import com.glodblock.github.extendedae.common.EAESingletons;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class ItemWirelessConnectTool extends Item {

    public ItemWirelessConnectTool(Properties props) {
        super(props.stacksTo(1));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull TooltipDisplay tooltipDisplay, @NotNull Consumer<Component> lines, @NotNull TooltipFlag tooltipFlags) {
        super.appendHoverText(stack, context, tooltipDisplay, lines, tooltipFlags);
        var locator = stack.get(EAESingletons.WIRELESS_LOCATOR);
        if (locator != null) {
            var freq = locator.left();
            var globalPos = locator.right();
            BlockPos pos = globalPos != null ? globalPos.pos() : BlockPos.ZERO;
            if (freq != 0) {
                lines.accept(Component.translatable("wireless.tooltip", pos.getX(), pos.getY(), pos.getZ()).withStyle(ChatFormatting.GRAY));
                lines.accept(Component.translatable("wireless.use.tooltip.02").withStyle(ChatFormatting.GRAY));
            } else {
                lines.accept(Component.translatable("wireless.use.tooltip.01").withStyle(ChatFormatting.GRAY));
            }
        } else {
            lines.accept(Component.translatable("wireless.use.tooltip.01").withStyle(ChatFormatting.GRAY));
        }
    }

    @Nonnull
    @Override
    public InteractionResult use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        var stack = player.getItemInHand(hand);
        if (InteractionUtil.isInAlternateUseMode(player) && stack.getItem() == EAESingletons.WIRELESS_TOOL.get()) {
            stack.remove(EAESingletons.WIRELESS_LOCATOR);
            player.sendOverlayMessage(Component.translatable("chat.wireless_connect.clear"));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

}
