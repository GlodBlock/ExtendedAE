package com.github.glodblock.epp.client.button;

import appeng.client.gui.style.Blitter;
import com.github.glodblock.epp.EPP;
import net.minecraft.resources.ResourceLocation;

public class EPPIcon {

    private static final ResourceLocation TEXTURE = EPP.id("textures/guis/nicons.png");

    public static final Blitter INFO = Blitter.texture(TEXTURE, 64, 64).src(0, 0, 16, 16);

}
