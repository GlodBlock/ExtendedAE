package com.glodblock.github.ae2netanalyser.client.gui.textures;

import appeng.client.gui.style.Blitter;
import com.glodblock.github.ae2netanalyser.AEAnalyser;
import net.minecraft.resources.Identifier;

public class Blitters {

    private static final Identifier TEXTURE = AEAnalyser.id("textures/gui/icons.png");

    public static final Blitter LEFT_ARROW = Blitter.texture(TEXTURE, 256, 256).src(0, 0, 10, 17);
    public static final Blitter LEFT_ARROW_ON = Blitter.texture(TEXTURE, 256, 256).src(10, 0, 10, 17);
    public static final Blitter LEFT_ARROW_DISABLE = Blitter.texture(TEXTURE, 256, 256).src(20, 0, 10, 17);
    public static final Blitter RIGHT_ARROW = Blitter.texture(TEXTURE, 256, 256).src(0, 17, 10, 17);
    public static final Blitter RIGHT_ARROW_ON = Blitter.texture(TEXTURE, 256, 256).src(10, 17, 10, 17);
    public static final Blitter RIGHT_ARROW_DISABLE = Blitter.texture(TEXTURE, 256, 256).src(20, 17, 10, 17);
    public static final Blitter SLIDER = Blitter.texture(TEXTURE, 256, 256).src(0, 34, 4, 7);
    public static final Blitter SLIDER_ON = Blitter.texture(TEXTURE, 256, 256).src(4, 34, 4, 7);
    public static final Blitter BIG_BUTTON = Blitter.texture(TEXTURE, 256, 256).src(30, 0, 67, 19);
    public static final Blitter BIG_BUTTON_SELECT = Blitter.texture(TEXTURE, 256, 256).src(30, 19, 67, 19);
    public static final Blitter BIG_BUTTON_HOVER = Blitter.texture(TEXTURE, 256, 256).src(30, 38, 67, 18);
    public static final Blitter CANCEL = Blitter.texture(TEXTURE, 256, 256).src(97, 0, 15, 16);
    public static final Blitter CANCEL_ON = Blitter.texture(TEXTURE, 256, 256).src(112, 0, 15, 16);
    public static final Blitter CONFIRM = Blitter.texture(TEXTURE, 256, 256).src(97, 16, 15, 16);
    public static final Blitter CONFIRM_ON = Blitter.texture(TEXTURE, 256, 256).src(112, 16, 15, 16);

}
