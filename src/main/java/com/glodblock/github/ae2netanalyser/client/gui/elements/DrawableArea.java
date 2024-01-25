package com.glodblock.github.ae2netanalyser.client.gui.elements;

import appeng.client.gui.AEBaseScreen;
import net.minecraft.client.gui.GuiGraphics;

public abstract class DrawableArea extends ClickableArea {

    public DrawableArea(int x, int y, int width, int height, AEBaseScreen<?> parent, Runnable job) {
        super(x, y, width, height, parent, job);
    }

    public abstract void draw(GuiGraphics guiGraphics);

}
