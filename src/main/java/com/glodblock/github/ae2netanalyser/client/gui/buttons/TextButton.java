package com.glodblock.github.ae2netanalyser.client.gui.buttons;

import appeng.client.gui.style.Blitter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;


public class TextButton extends FancyButton {

    private final Background bg;
    private final Component text;

    public TextButton(Background background, Component text, Runnable job) {
        super(background.getWidth(), background.getHeight(), job);
        this.bg = background;
        this.text = text;
    }

    @Override
    protected void render(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        var offset = 0;
        var color = 0xf2f2f2 | Mth.ceil(this.alpha * 255.0F) << 24;
        if (this.isHovered()) {
            this.bg.hover.dest(this.getX(), this.getY() + 1).blit(graphics);
            offset = 1;
            color = 0x517497 | Mth.ceil(this.alpha * 255.0F) << 24;
        } else if (this.isFocused()) {
            this.bg.selected.dest(this.getX(), this.getY()).blit(graphics);
        } else {
            this.bg.normal.dest(this.getX(), this.getY()).blit(graphics);
        }
        var font = Minecraft.getInstance().font;
        var width = font.width(this.text);
        var height = font.lineHeight;
        var centerX = this.getX() + this.getWidth() / 2;
        var centerY = this.getY() + this.getHeight() / 2;
        graphics.text(font, this.text, centerX - width / 2, centerY - height / 2 + offset, color, false);
    }

    public record Background(Blitter normal, Blitter hover, Blitter selected) {

        private int getWidth() {
            return this.normal.getSrcWidth();
        }

        private int getHeight() {
            return this.normal.getSrcHeight();
        }

    }

}
