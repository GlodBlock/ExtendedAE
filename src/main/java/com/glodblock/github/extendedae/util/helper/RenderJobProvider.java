package com.glodblock.github.extendedae.util.helper;

import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

public interface RenderJobProvider {

    default Runnable getRenderJob(RenderLevelStageEvent.Stage stage) {
        return () -> {};
    }

}
