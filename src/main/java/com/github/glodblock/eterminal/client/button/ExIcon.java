package com.github.glodblock.eterminal.client.button;

import appeng.client.gui.style.Blitter;
import com.github.glodblock.eterminal.EnhancedTerminal;
import net.minecraft.resources.ResourceLocation;

public class ExIcon {

    private static final ResourceLocation TEXTURE = EnhancedTerminal.id("textures/guis/nicons.png");
    public static final Blitter HIGHLIGHT_BLOCK = Blitter.texture(TEXTURE, 64, 64).src(16, 0, 16, 16);


}
