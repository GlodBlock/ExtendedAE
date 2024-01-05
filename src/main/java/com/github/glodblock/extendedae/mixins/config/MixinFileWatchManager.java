package com.github.glodblock.extendedae.mixins.config;

import com.github.glodblock.extendedae.util.mixinutil.IConfigStop;
import dev.toma.configuration.config.io.FileWatchManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.io.IOException;
import java.nio.file.WatchService;
import java.util.concurrent.ScheduledExecutorService;

@Mixin(FileWatchManager.class)
public abstract class MixinFileWatchManager implements IConfigStop {

    @Final
    @Shadow(remap = false)
    private WatchService service;

    @Final
    @Shadow(remap = false)
    private ScheduledExecutorService executorService;

    @Unique
    @Override
    public void extendedae_$stop() {
        try {
            executorService.shutdown();
            service.close();
        } catch (IOException e) {
            throw new IllegalStateException("Error while stopping FileWatch service", e);
        }
    }

}
