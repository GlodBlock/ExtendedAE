package com.glodblock.github.ae2netanalyser.client.gui.buttons;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public abstract class FancyButton extends Button {

    protected FancyButton(int width, int height, Runnable job) {
        super(0, 0, width, height, Component.empty(), _ -> job.run(), Button.DEFAULT_NARRATION);
    }

    @Override
    protected void extractContents(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        if (this.visible) {
            this.render(graphics, mouseX, mouseY, a);
        }
    }

    protected abstract void render(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a);

}
