package com.glodblock.github.extendedae.client.hooks;

import com.glodblock.github.extendedae.client.gui.GuiTagExportBus;
import com.glodblock.github.extendedae.client.gui.GuiTagStorageBus;
import com.mojang.blaze3d.platform.InputConstants;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.stream.Stream;

public class TagHook {

    public static void onInit() {
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, (ItemTooltipEvent evt) -> hookTooltip(evt.getItemStack(), evt.getToolTip(), evt.getEntity()));
    }

    @SuppressWarnings("deprecation")
    private static void hookTooltip(ItemStack stack, List<Component> tooltip, Player player) {
        if (Minecraft.getInstance().screen instanceof GuiTagExportBus || Minecraft.getInstance().screen instanceof GuiTagStorageBus) {
            if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), GLFW.GLFW_KEY_RIGHT_SHIFT)) {
                Holder.Reference<@NotNull Block> blockHolder = null;
                boolean anyTag = false;
                if (stack.getItem() instanceof BlockItem block) {
                    blockHolder = block.getBlock().builtInRegistryHolder();
                }
                if (Stream.concat(stack.tags(), blockHolder == null ? Stream.empty() : blockHolder.tags()).findAny().isPresent()) {
                    tooltip.add(Component.translatable("tag_display.tooltip.items").withStyle(ChatFormatting.YELLOW));
                    Stream.concat(stack.tags(), blockHolder == null ? Stream.empty() : blockHolder.tags()).limit(128).forEach(
                            key -> tooltip.add(Component.literal(key.location().toString()).withStyle(ChatFormatting.GREEN))
                    );
                    anyTag = true;
                }

                var fluidCap = stack.getCapability(Capabilities.Fluid.ITEM, ItemAccess.forStack(stack));
                if (fluidCap != null) {
                    ReferenceSet<TagKey<@NotNull Fluid>> fluidSet = new ReferenceOpenHashSet<>();
                    for (var tank = 0; tank < fluidCap.size(); tank ++) {
                        var fluid = fluidCap.getResource(tank);
                        if (!fluid.isEmpty()) {
                            fluid.tags().forEach(fluidSet::add);
                        }
                    }
                    if (!fluidSet.isEmpty()) {
                        tooltip.add(Component.translatable("tag_display.tooltip.fluids").withStyle(ChatFormatting.YELLOW));
                        fluidSet.stream().limit(128).forEach(
                                key -> tooltip.add(Component.literal(key.location().toString()).withStyle(ChatFormatting.GREEN))
                        );
                        anyTag = true;
                    }
                }
                if (!anyTag) {
                    tooltip.add(Component.translatable("tag_display.tooltip.no_tags").withStyle(ChatFormatting.YELLOW));
                }
            } else {
                tooltip.add(Component.translatable("tag_display.tooltip.hint").withStyle(ChatFormatting.YELLOW));
            }
        }
    }

}
