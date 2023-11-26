package com.github.glodblock.extendedae.client.button;

import appeng.client.gui.style.Blitter;
import com.github.glodblock.extendedae.client.render.HighlightHandler;
import com.github.glodblock.extendedae.util.FCUtil;
import net.minecraft.client.gui.components.Button;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class HighlightButton extends EPPButton {

    private float multiplier;
    private Runnable successJob;
    private BlockPos pos;
    private ResourceKey<Level> dim;

    public HighlightButton() {
        super(HighlightButton::highlight);
    }

    public void setMultiplier(double val) {
        this.multiplier = (float) FCUtil.clamp(val, 1, 30);
    }

    public void setSuccessJob(Runnable process) {
        this.successJob = process;
    }

    public void setTarget(BlockPos pos, ResourceKey<Level> world) {
        this.pos = pos;
        this.dim = world;
    }

    private static void highlight(Button btn) {
        if (btn instanceof HighlightButton hb) {
            if (hb.dim != null && hb.pos != null) {
                HighlightHandler.highlight(hb.pos, hb.dim, System.currentTimeMillis() + (long) (600 * hb.multiplier));
                if (hb.successJob != null) {
                    hb.successJob.run();
                }
            }
        }
    }

    @Override
    public Blitter getBlitterIcon() {
        return EPPIcon.HIGHLIGHT_BLOCK;
    }
}
