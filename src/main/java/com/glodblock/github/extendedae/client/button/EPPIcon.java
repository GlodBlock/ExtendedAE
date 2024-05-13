package com.glodblock.github.extendedae.client.button;

import appeng.client.gui.style.Blitter;
import com.glodblock.github.extendedae.ExtendedAE;
import net.minecraft.resources.ResourceLocation;

public class EPPIcon {

    private static final ResourceLocation TEXTURE = ExtendedAE.id("textures/guis/nicons.png");

    public static final Blitter INFO = Blitter.texture(TEXTURE, 64, 64).src(0, 0, 16, 16);
    public static final Blitter HIGHLIGHT_BLOCK = Blitter.texture(TEXTURE, 64, 64).src(16, 0, 16, 16);
    public static final Blitter LEFT = Blitter.texture(TEXTURE, 64, 64).src(32, 0, 16, 16);
    public static final Blitter RIGHT = Blitter.texture(TEXTURE, 64, 64).src(48, 0, 16, 16);
    public static final Blitter STACK_1 = Blitter.texture(TEXTURE, 64, 64).src(0, 16, 16, 16);
    public static final Blitter STACK_64 = Blitter.texture(TEXTURE, 64, 64).src(16, 16, 16, 16);
    public static final Blitter FILLED = Blitter.texture(TEXTURE, 64, 64).src(32, 16, 16, 16);
    public static final Blitter BUCKET = Blitter.texture(TEXTURE, 64, 64).src(48, 16, 16, 16);
    public static final Blitter OVER_STACK = Blitter.texture(TEXTURE, 64, 64).src(0, 32, 16, 16);
    public static final Blitter BELOW_STACK = Blitter.texture(TEXTURE, 64, 64).src(16, 32, 16, 16);

}
