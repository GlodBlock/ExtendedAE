package com.glodblock.github.ae2netanalyser.client.gui.textures;

import appeng.client.gui.style.Blitter;
import com.glodblock.github.ae2netanalyser.AEAnalyser;
import net.minecraft.resources.ResourceLocation;

public class Blitters {

    private static final ResourceLocation TEXTURE = AEAnalyser.id("textures/gui/color_configer.png");

    public static final Blitter COLOR_SUB_MENU = Blitter.texture(TEXTURE, 200, 200).src(0, 0, 110, 80);
    public static final Blitter SLIDER = Blitter.texture(TEXTURE, 200, 200).src(110, 0, 4, 7);

}
