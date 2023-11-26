package com.github.glodblock.extendedae.client.button;

import appeng.client.gui.style.Blitter;

public class TooltipIcon extends EPPButton {

    public TooltipIcon() {
        super(b -> {});
        this.setDisableClickSound(true);
    }

    @Override
    Blitter getBlitterIcon() {
        return EPPIcon.INFO;
    }
}
