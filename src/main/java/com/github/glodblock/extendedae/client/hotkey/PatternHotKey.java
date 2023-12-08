package com.github.glodblock.extendedae.client.hotkey;

import appeng.crafting.pattern.EncodedPatternItem;
import com.github.glodblock.extendedae.EAE;
import com.github.glodblock.extendedae.network.EAENetworkServer;
import com.github.glodblock.extendedae.network.packet.CPatternKey;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class PatternHotKey {

    private static final KeyMapping VIEW_PATTERN = new KeyMapping("key.epp.viewpattern", GLFW.GLFW_KEY_P, "key.epp.category");
    private static final ResourceLocation KEY = EAE.id("view_pattern");

    public static void init() {
        KeyBindingHelper.registerKeyBinding(PatternHotKey.getHotKey());
        ItemTooltipCallback.EVENT.register(KEY, PatternHotKey::hookTooltip);
        ItemTooltipCallback.EVENT.addPhaseOrdering(Event.DEFAULT_PHASE, KEY);
    }

    private static void hookTooltip(ItemStack stack, TooltipFlag tooltipFlag, List<Component> tooltip) {
        if (isKeyBound() && stack.getItem() instanceof EncodedPatternItem) {
            tooltip.add(1, Component.translatable("pattern.tooltip", VIEW_PATTERN.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));
            if (pressed()) {
                EAENetworkServer.INSTANCE.sendToServer(new CPatternKey(stack));
            }
        }
    }

    private static boolean pressed() {
        var keyCode = KeyBindingHelper.getBoundKeyOf(VIEW_PATTERN).getValue();
        var window = Minecraft.getInstance().getWindow().getWindow();
        return InputConstants.isKeyDown(window, keyCode);
    }

    private static boolean isKeyBound() {
        return !VIEW_PATTERN.isUnbound();
    }

    public static KeyMapping getHotKey() {
        return VIEW_PATTERN;
    }

}
