package com.glodblock.github.ae2netanalyser.client.gui.elements;

import com.glodblock.github.glodium.client.render.ColorData;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

public class ColorArea extends DrawableArea {

    private ColorData color = new ColorData(1f, 1f, 1f);

    public ColorArea(int x, int y, int width, int height, AbstractContainerScreen<?> parent, Runnable job) {
        super(x, y, width, height, parent, job);
    }

    public void setColor(ColorData color) {
        this.color = color;
    }

    public ColorData getColor() {
        return this.color;
    }

    @Override
    public void draw(GuiGraphicsExtractor guiGraphics) {
        int x = this.x + this.screen.getLeftPos();
        int y = this.y + this.screen.getTopPos();
        guiGraphics.fill(x, y, x + this.w, y + this.h, this.color.toARGB());
    }
}
