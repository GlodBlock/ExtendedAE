package com.github.glodblock.epp.client.button;

import appeng.client.gui.style.Blitter;
import com.github.glodblock.epp.client.render.HighlightHandler;
import com.github.glodblock.epp.util.FCClientUtil;
import com.github.glodblock.epp.util.FCUtil;
import net.minecraft.client.gui.components.Button;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

public class HighlightButton extends EPPButton {

    private float multiplier;
    private Runnable successJob;
    private BlockPos pos;
    private ResourceKey<Level> dim;
    @Nullable
    private Direction face;

    public HighlightButton() {
        super(HighlightButton::highlight);
    }

    public void setMultiplier(double val) {
        this.multiplier = (float) FCUtil.clamp(val, 1, 30);
    }

    public void setSuccessJob(Runnable process) {
        this.successJob = process;
    }

    public void setTarget(BlockPos pos, Direction face, ResourceKey<Level> world) {
        this.pos = pos;
        this.dim = world;
        this.face = face;
    }

    public void setTarget(BlockPos pos, ResourceKey<Level> world) {
        this.setTarget(pos, null, world);
    }

    private static void highlight(Button btn) {
        if (btn instanceof HighlightButton hb) {
            if (hb.dim != null && hb.pos != null) {
                if (hb.face == null) {
                    HighlightHandler.highlight(hb.pos, hb.dim, System.currentTimeMillis() + (long) (600 * hb.multiplier));
                } else {
                    var origin = getNorthPartModel().move(hb.pos);
                    var center = new AABB(hb.pos).getCenter();
                    switch (hb.face) {
                        case WEST -> origin = FCClientUtil.rotor(origin, center, Direction.Axis.Y, (float) (Math.PI / 2));
                        case SOUTH -> origin = FCClientUtil.rotor(origin, center, Direction.Axis.Y, (float) Math.PI);
                        case EAST -> origin = FCClientUtil.rotor(origin, center, Direction.Axis.Y, (float) (-Math.PI / 2));
                        case UP -> origin = FCClientUtil.rotor(origin, center, Direction.Axis.X, (float) (-Math.PI / 2));
                        case DOWN -> origin = FCClientUtil.rotor(origin, center, Direction.Axis.X, (float) (Math.PI / 2));
                    }
                    HighlightHandler.highlight(hb.pos, hb.face, hb.dim, System.currentTimeMillis() + (long) (600 * hb.multiplier), origin);
                }
                if (hb.successJob != null) {
                    hb.successJob.run();
                }
            }
        }
    }

    private static AABB getNorthPartModel() {
        return new AABB(2 / 16D, 2 / 16D, 0, 14 / 16D, 14 / 16D, 2 / 16D);
    }

    @Override
    public Blitter getBlitterIcon() {
        return EPPIcon.HIGHLIGHT_BLOCK;
    }
}
