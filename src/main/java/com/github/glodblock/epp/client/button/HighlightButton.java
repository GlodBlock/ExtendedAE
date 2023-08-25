package com.github.glodblock.epp.client.button;

import appeng.client.gui.style.Blitter;
import net.minecraft.world.entity.player.Player;

public class HighlightButton extends EPPButton {

    public float multiplier;
    public Player player;

    public HighlightButton(OnPress onPress, float dis, Player player) {
        super(onPress);
        this.multiplier = Math.min(Math.max(1f, dis), 25f);
        this.player = player;
    }

    @Override
    Blitter getBlitterIcon() {
        return EPPIcon.HIGHLIGHT_BLOCK;
    }
}
