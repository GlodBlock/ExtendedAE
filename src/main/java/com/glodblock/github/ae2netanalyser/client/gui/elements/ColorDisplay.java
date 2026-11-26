package com.glodblock.github.ae2netanalyser.client.gui.elements;

import com.glodblock.github.glodium.client.render.ColorData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ColorDisplay extends AbstractWidget {

    private final Supplier<ColorData> color;

    public ColorDisplay(Supplier<ColorData> color) {
        super(0, 0, 0, 0, Component.empty());
        this.color = color;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float a) {
        var color = this.color.get();
        if (this.visible && color != null) {
            graphics.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), color.toARGB());
        }
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
        // NO-OP
    }

}
