package com.glodblock.github.extendedae.client.hotkey;

import appeng.crafting.pattern.EncodedPatternItem;
import com.glodblock.github.extendedae.network.EAENetworkHandler;
import com.glodblock.github.extendedae.network.packet.CPatternKey;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.List;

public class PatternHotKey {

    public static void onInit() {
        NeoForge.EVENT_BUS.addListener((ItemTooltipEvent evt) -> hookTooltip(evt.getItemStack(), evt.getToolTip()));
    }

    private static void hookTooltip(ItemStack stack, List<Component> tooltip) {
        if (EAEHotKey.isKeyBound(EAEHotKey.VIEW_PATTERN) && stack.getItem() instanceof EncodedPatternItem) {
            tooltip.add(1, Component.translatable("pattern.tooltip", EAEHotKey.VIEW_PATTERN.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));
            if (EAEHotKey.pressed(EAEHotKey.VIEW_PATTERN)) {
                EAENetworkHandler.INSTANCE.sendToServer(new CPatternKey(stack));
            }
        }
    }

}
