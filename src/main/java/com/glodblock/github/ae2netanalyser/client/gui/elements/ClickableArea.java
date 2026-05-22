package com.glodblock.github.ae2netanalyser.client.gui.elements;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

public class ClickableArea {

    public int x;
    public int y;
    public int w;
    public int h;
    protected final AbstractContainerScreen<?> screen;
    protected final Runnable job;

    public ClickableArea(int x, int y, int width, int height, AbstractContainerScreen<?> parent, Runnable job) {
        this.x = x;
        this.y = y;
        this.w = width;
        this.h = height;
        this.screen = parent;
        this.job = job;
    }

    public boolean isMouseOver(double x, double y) {
        return x >= this.x + this.screen.getLeftPos() && x < this.x + this.screen.getLeftPos() + this.w &&
                y >= this.y + this.screen.getTopPos() && y < this.y + this.screen.getTopPos() + this.h;
    }

    public boolean click(double x, double y) {
        if (isMouseOver(x, y)) {
            job.run();
            return true;
        }
        return false;
    }

    public void release(double x, double y) {

    }

}
