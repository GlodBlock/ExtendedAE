package com.glodblock.github.extendedae.client.hotkey;

import appeng.crafting.pattern.EncodedPatternItem;
import com.glodblock.github.extendedae.network.EAENetworkHandler;
import com.glodblock.github.extendedae.network.packet.CPatternKey;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class PatternHotKey {

    private static final KeyMapping VIEW_PATTERN = new KeyMapping("key.extendedae.viewpattern", GLFW.GLFW_KEY_P, "key.extendedae.category");

    static {
        VIEW_PATTERN.setKeyConflictContext(KeyConflictContext.GUI);
    }

    public static void onInit() {
        NeoForge.EVENT_BUS.addListener((ItemTooltipEvent evt) -> hookTooltip(evt.getItemStack(), evt.getToolTip()));
    }

    private static void hookTooltip(ItemStack stack, List<Component> tooltip) {
        if (isKeyBound() && stack.getItem() instanceof EncodedPatternItem) {
            tooltip.add(1, Component.translatable("pattern.tooltip", VIEW_PATTERN.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));
            if (pressed()) {
                EAENetworkHandler.INSTANCE.sendToServer(new CPatternKey(stack));
            }
        }
    }

    private static boolean pressed() {
        int keyCode = VIEW_PATTERN.getKey().getValue();
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
