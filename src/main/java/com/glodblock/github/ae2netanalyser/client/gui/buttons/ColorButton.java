package com.glodblock.github.ae2netanalyser.client.gui.buttons;

import com.glodblock.github.glodium.client.render.ColorData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ColorButton extends Button {

    private final Supplier<ColorData> color;

    public ColorButton(Runnable job, Supplier<ColorData> color) {
        super(0, 0, 0, 0, Component.empty(), b -> job.run(), Button.DEFAULT_NARRATION);
        this.color = color;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float a) {
        var color = this.color.get();
        if (this.visible && color != null) {
            graphics.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), color.toARGB());
        }
    }

}
