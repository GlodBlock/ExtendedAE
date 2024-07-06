package com.glodblock.github.extendedae.client.button;

import appeng.client.gui.style.Blitter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;

public class HighlightButtonSmall extends HighlightButton {

    public HighlightButtonSmall() {
        this.width = 6;
        this.height = 11;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {
        if (this.visible) {
            Blitter blitter = EPPIcon.TERMINAL_LOCATION;
            blitter.dest(getX(), getY()).zOffset(3).blit(guiGraphics);
        }
    }

    @Override
    public Rect2i getTooltipArea() {
        return new Rect2i(this.getX(), this.getY(), 6, 11);
    }

}
