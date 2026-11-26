package com.glodblock.github.ae2netanalyser.client.gui.buttons;

import appeng.client.gui.style.Blitter;
import com.glodblock.github.ae2netanalyser.client.gui.textures.Blitters;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

public class ChooseButton extends FancyButton {

    private final Choice choice;

    public ChooseButton(Choice choice, Runnable job) {
        super(15, 16, job);
        this.choice = choice;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float a) {
        if (this.isHovered()) {
            this.choice.hover.dest(this.getX(), this.getY()).blit(graphics);
        } else {
            this.choice.normal.dest(this.getX(), this.getY()).blit(graphics);
        }
    }

    public enum Choice {

        OK(Blitters.CONFIRM, Blitters.CONFIRM_ON),
        CANCEL(Blitters.CANCEL, Blitters.CANCEL_ON),;

        private final Blitter normal;
        private final Blitter hover;

        Choice(Blitter normal, Blitter hover) {
            this.normal = normal;
            this.hover = hover;
        }

    }

}
