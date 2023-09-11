package com.github.glodblock.epp.client.hotkey;

import appeng.crafting.pattern.EncodedPatternItem;
import com.github.glodblock.epp.network.EPPNetworkHandler;
import com.github.glodblock.epp.network.packet.CPatternKey;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class PatternHotKey {

    private static final KeyMapping VIEW_PATTERN = new KeyMapping("key.epp.viewpattern", GLFW.GLFW_KEY_P, "key.epp.category");

    static {
        VIEW_PATTERN.setKeyConflictContext(KeyConflictContext.GUI);
    }

    public static void init() {
        MinecraftForge.EVENT_BUS.addListener((ItemTooltipEvent evt) -> hookTooltip(evt.getItemStack(), evt.getToolTip()));
    }

    private static void hookTooltip(ItemStack stack, List<Component> tooltip) {
        if (isKeyBound() && stack.getItem() instanceof EncodedPatternItem) {
            tooltip.add(1, Component.translatable("pattern.tooltip", VIEW_PATTERN.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));
            if (pressed()) {
                EPPNetworkHandler.INSTANCE.sendToServer(new CPatternKey(stack));
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
