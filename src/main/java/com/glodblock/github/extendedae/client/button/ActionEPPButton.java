package com.glodblock.github.extendedae.client.button;

import appeng.client.gui.style.Blitter;
import appeng.util.Icon;

public class ActionEPPButton extends EPPButton {

    private final Blitter icon;

    public ActionEPPButton(OnPress onPress, Blitter icon) {
        super(onPress);
        this.icon = icon;
    }

    public ActionEPPButton(OnPress onPress, Icon icon) {
        super(onPress);
        this.icon = Blitter.icon(icon);
    }

    @Override
    protected Blitter getBlitterIcon() {
        return icon;
    }

}
