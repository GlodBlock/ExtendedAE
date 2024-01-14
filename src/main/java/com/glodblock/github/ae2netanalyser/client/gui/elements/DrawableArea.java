package com.glodblock.github.ae2netanalyser.client.gui.elements;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

public abstract class DrawableArea extends ClickableArea {

    public DrawableArea(int x, int y, int width, int height, AbstractContainerScreen<?> parent, Runnable job) {
        super(x, y, width, height, parent, job);
    }

    public abstract void draw(GuiGraphics guiGraphics);

}
