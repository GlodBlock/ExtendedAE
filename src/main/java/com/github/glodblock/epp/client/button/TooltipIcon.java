package com.github.glodblock.epp.client.button;

import appeng.client.gui.Icon;
import appeng.client.gui.widgets.IconButton;

public class TooltipIcon extends IconButton {

    public TooltipIcon() {
        super(b -> {});
        this.setDisableClickSound(true);
    }

    @Override
    protected Icon getIcon() {
        return Icon.HELP;
    }
}
