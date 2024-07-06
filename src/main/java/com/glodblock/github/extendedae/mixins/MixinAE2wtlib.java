package com.glodblock.github.extendedae.mixins;

import com.glodblock.github.extendedae.xmod.wt.WTCommonLoad;
import de.mari_023.ae2wtlib.AE2wtlib;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AE2wtlib.class)
public abstract class MixinAE2wtlib {

    @Inject(
            method = "onAe2Initialized",
            at = @At("HEAD"),
            remap = false
    )
    private static void registerTerminal(CallbackInfo ci) {
        WTCommonLoad.init();
    }

}
