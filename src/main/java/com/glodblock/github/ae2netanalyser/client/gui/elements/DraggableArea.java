package com.glodblock.github.ae2netanalyser.client.gui.elements;

import com.glodblock.github.ae2netanalyser.client.gui.textures.Blitters;
import com.glodblock.github.glodium.util.GlodUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

public class DraggableArea extends DrawableArea {

    private double offset = 0;
    private boolean active = false;
    private float value;

    public DraggableArea(int x, int y, int width, int height, AbstractContainerScreen<?> parent) {
        super(x, y, width, height, parent, () -> {});
    }

    public float getValue() {
        return this.value;
    }

    public void setValue(float value) {
        this.value = (float) GlodUtil.clamp(value, 0, 1);
    }

    @Override
    public boolean click(double x, double y) {
        if (isMouseOver(x, y)) {
            this.active = true;
            this.offset = x;
            return true;
        }
        return false;
    }

    @Override
    public void release(double x, double y) {
        this.active = false;
    }

    public void drag(double x, double y) {
        if (this.active) {
            double move = (x - this.offset) / this.w;
            this.offset = x;
            this.value = (float) GlodUtil.clamp(this.value + move, 0, 1);
        }
    }

    @Override
    public void draw(GuiGraphics guiGraphics) {
        int x = this.x + this.screen.getGuiLeft();
        int y = this.y + this.screen.getGuiTop();
        Blitters.SLIDER.dest((int) (x + this.value * this.w), y).blit(guiGraphics);
    }

    public boolean isMouseOver(double x, double y) {
        int offsetX = (int) (this.value * this.w);
        return x >= this.x + this.screen.getGuiLeft() + offsetX && x < this.x + this.screen.getGuiLeft() + this.w + offsetX &&
                y >= this.y + this.screen.getGuiTop() && y < this.y + this.screen.getGuiTop() + this.h;
    }

}
