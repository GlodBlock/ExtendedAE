package com.glodblock.github.ae2netanalyser.client.gui.elements;

import appeng.client.gui.AEBaseScreen;

public class ClickableArea {

    public int x;
    public int y;
    public int w;
    public int h;
    protected final AEBaseScreen<?> screen;
    protected final Runnable job;

    public ClickableArea(int x, int y, int width, int height, AEBaseScreen<?> parent, Runnable job) {
        this.x = x;
        this.y = y;
        this.w = width;
        this.h = height;
        this.screen = parent;
        this.job = job;
    }

    public boolean isMouseOver(double x, double y) {
        return x >= this.x + this.screen.getGuiLeft() && x < this.x + this.screen.getGuiLeft() + this.w &&
                y >= this.y + this.screen.getGuiTop() && y < this.y + this.screen.getGuiTop() + this.h;
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

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

}
