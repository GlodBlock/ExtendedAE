package com.glodblock.github.extendedae.client.hooks;

import com.glodblock.github.extendedae.client.gui.GuiTagExportBus;
import com.glodblock.github.extendedae.client.gui.GuiTagStorageBus;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.List;

public class TagHook {

    public static void onInit() {
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, (ItemTooltipEvent evt) -> hookTooltip(evt.getItemStack(), evt.getToolTip()));
    }

    private static void hookTooltip(ItemStack stack, List<Component> tooltip) {
        if (Minecraft.getInstance().screen instanceof GuiTagExportBus || Minecraft.getInstance().screen instanceof GuiTagStorageBus) {
            if (Screen.hasShiftDown()) {
                var holder = stack.getItemHolder();
                boolean anyTag = false;
                if (holder.tags().findAny().isPresent()) {
                    tooltip.add(Component.translatable("tag_display.tooltip.items").withStyle(ChatFormatting.YELLOW));
                    holder.tags().limit(128).forEach(
                            key -> tooltip.add(Component.literal(key.location().toString()).withStyle(ChatFormatting.GREEN))
                    );
                    anyTag = true;
                }
                var fluidCap = stack.getCapability(Capabilities.FluidHandler.ITEM);
                if (fluidCap != null) {
                    ReferenceSet<TagKey<Fluid>> fluidSet = new ReferenceOpenHashSet<>();
                    for (var tank = 0; tank < fluidCap.getTanks(); tank ++) {
                        var fluid = fluidCap.getFluidInTank(tank);
                        if (!fluid.isEmpty()) {
                            fluid.getFluidHolder().tags().forEach(fluidSet::add);
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
