package com.glodblock.github.ae2netanalyser.client.gui.buttons;

import com.glodblock.github.ae2netanalyser.client.gui.textures.Blitters;
import com.glodblock.github.glodium.util.GlodUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class DragBar extends AbstractWidget {

    private final OnDrag job;
    private double value;
    private boolean selected;

    public DragBar(double value, OnDrag job) {
        super(0, 0, 0, 0, Component.empty());
        this.value = GlodUtil.clamp(value, 0, 1);
        this.job = job;
    }

    public double getValue() {
        return this.value;
    }

    public void setValue(double value) {
        this.value = GlodUtil.clamp(value, 0, 1);
        this.job.onDrag(this);
    }

    @Override
    public void onClick(@NotNull MouseButtonEvent event, boolean doubleClick) {
        double offset = Math.round(event.x() - this.getX());
        this.setValue(offset / this.getWidth());
        this.selected = true;
    }

    @Override
    public void onRelease(@NotNull MouseButtonEvent event) {
        this.selected = false;
    }

    @Override
    protected void onDrag(@NotNull MouseButtonEvent event, double dx, double dy) {
        if (this.selected) {
            this.setValue(this.value + dx / this.getWidth());
        }
    }

    @Override
    protected void extractWidgetRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        if (this.visible) {
            if (this.isHovered()) {
                Blitters.SLIDER_ON.dest((int) (this.getX() + this.value * (this.getWidth() - Blitters.SLIDER_ON.getSrcWidth())), this.getY()).blit(graphics);
            } else {
                Blitters.SLIDER.dest((int) (this.getX() + this.value * (this.getWidth() - Blitters.SLIDER_ON.getSrcWidth())), this.getY()).blit(graphics);
            }
        }
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }

    public interface OnDrag {

        void onDrag(DragBar bar);

    }

}
