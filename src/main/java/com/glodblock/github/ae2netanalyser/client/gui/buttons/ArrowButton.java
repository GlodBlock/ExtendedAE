package com.glodblock.github.ae2netanalyser.client.gui.buttons;

import appeng.client.gui.style.Blitter;
import com.glodblock.github.ae2netanalyser.client.gui.textures.Blitters;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

import java.util.function.BooleanSupplier;

public class ArrowButton extends FancyButton {

    private final ArrowDirection type;
    private final BooleanSupplier rangeChecker;

    public ArrowButton(ArrowDirection type, Runnable job, BooleanSupplier rangeChecker) {
        super(10, 17, job);
        this.type = type;
        this.rangeChecker = rangeChecker;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float a) {
        if (this.rangeChecker.getAsBoolean()) {
            if (this.isHovered()) {
                this.type.hover.dest(this.getX(), this.getY()).blit(graphics);
            } else {
                this.type.normal.dest(this.getX(), this.getY()).blit(graphics);
            }
        } else {
            this.type.disable.dest(this.getX(), this.getY()).blit(graphics);
        }
    }

    public enum ArrowDirection {

        LEFT(Blitters.LEFT_ARROW, Blitters.LEFT_ARROW_ON, Blitters.LEFT_ARROW_DISABLE),
        RIGHT(Blitters.RIGHT_ARROW, Blitters.RIGHT_ARROW_ON, Blitters.RIGHT_ARROW_DISABLE),;

        private final Blitter normal;
        private final Blitter hover;
        private final Blitter disable;

        ArrowDirection(Blitter normal, Blitter hover, Blitter disable) {
            this.normal = normal;
            this.hover = hover;
            this.disable = disable;
        }

    }

}
