package com.github.glodblock.extendedae.mixins.config;

import com.github.glodblock.extendedae.util.mixinutil.IConfigStop;
import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.io.ConfigIO;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Configuration.class)
public abstract class MixinConfiguration {

    @Inject(
            method = "<init>",
            at = @At("TAIL"),
            remap = false
    )
    private void stopServer(CallbackInfo ci) {
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            if (server instanceof DedicatedServer) {
                ((IConfigStop) (Object) ConfigIO.FILE_WATCH_MANAGER).extendedae_$stop();
            }
        });
    }

}
