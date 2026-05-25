package com.glodblock.github.extendedae.client.hotkey;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class EAEHotKey {

    public static final KeyMapping VIEW_PATTERN = new KeyMapping("key.extendedae.viewpattern", GLFW.GLFW_KEY_P, "key.extendedae.category");
    public static final KeyMapping SET_AMOUNT = new KeyMapping("key.extendedae.set_amount", InputConstants.Type.MOUSE, 2, "key.extendedae.category");

    static {
        VIEW_PATTERN.setKeyConflictContext(KeyConflictContext.GUI);
        SET_AMOUNT.setKeyConflictContext(KeyConflictContext.GUI);
    }

    public static boolean pressed(KeyMapping hotkey) {
        int keyCode = hotkey.getKey().getValue();
        var window = Minecraft.getInstance().getWindow().getWindow();
        return InputConstants.isKeyDown(window, keyCode);
    }

    public static boolean isKeyBound(KeyMapping hotkey) {
        return !hotkey.isUnbound();
    }

}
