package com.glodblock.github.extendedae.client.button;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.Rect2i;

public class HighlightButtonSmall extends HighlightButton {

    public HighlightButtonSmall() {
        this.width = 6;
        this.height = 11;
    }

    @Override
    public void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partial) {
        if (this.visible) {
            EPPIcon.TERMINAL_LOCATION.dest(getX(), getY()).blit(guiGraphics);
        }
    }

    @Override
    public Rect2i getTooltipArea() {
        return new Rect2i(this.getX(), this.getY(), 6, 11);
    }

}
