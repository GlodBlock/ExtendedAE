package com.glodblock.github.ae2netanalyser.client.gui.buttons;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public abstract class FancyButton extends Button {

    protected FancyButton(int width, int height, Runnable job) {
        super(0, 0, width, height, Component.empty(), b -> job.run(), Button.DEFAULT_NARRATION);
    }

}
