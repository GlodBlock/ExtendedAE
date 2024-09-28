package com.glodblock.github.extendedae.client.button;

import appeng.client.gui.Icon;
import appeng.client.gui.style.Blitter;

public class ActionEPPButton extends EPPButton {

    private final Blitter icon;

    public ActionEPPButton(OnPress onPress, Icon icon) {
        super(onPress);
        this.icon = icon.getBlitter();
    }

    @Override
    protected Blitter getBlitterIcon() {
        return icon;
    }

}
