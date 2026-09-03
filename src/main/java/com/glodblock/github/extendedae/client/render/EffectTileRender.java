package com.glodblock.github.extendedae.client.render;

import com.glodblock.github.extendedae.util.helper.RenderJobProvider;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

public class EffectTileRender {

    public static void hook(RenderLevelStageEvent event) {
        if (Minecraft.getInstance().levelRenderer instanceof RenderJobProvider provider) {
            var job = provider.getRenderJob(event.getStage());
            job.run();
            Minecraft.getInstance().renderBuffers().bufferSource().endBatch();
        }
    }

}
