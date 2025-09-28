package com.glodblock.github.extendedae.util;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class MessageUtil {

    private MessageUtil() {}

    public  static Component createEnhancedHighlightMessage(Player player, BlockPos targetPos, ResourceKey<Level> targetDimension, String translatable) {

        String dimensionId = targetDimension.location().toString();

        int distance = (int) Math.sqrt(player.blockPosition().distSqr(targetPos));

        String tpCommand = "/tp @s " + targetPos.getX() + " " + targetPos.getY() + " " + targetPos.getZ();
        Component coordsComponent = Component.literal("[" + targetPos.toShortString() + "]")
                .withStyle(style -> style
                        .withColor(ChatFormatting.GREEN)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, tpCommand))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.coordinates.tooltip")))
                );

        String dimTpCommand = "/execute in " + dimensionId + " run tp @s " + targetPos.getX() + " " + targetPos.getY() + " " + targetPos.getZ();
        Component dimensionComponent = Component.literal(dimensionId)
                .withStyle(style -> style
                        .withColor(ChatFormatting.AQUA)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, dimTpCommand))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.coordinates.tooltip")))
                );

        return Component.translatable(
                translatable,
                coordsComponent,
                dimensionComponent,
                distance
        );
    }
}
