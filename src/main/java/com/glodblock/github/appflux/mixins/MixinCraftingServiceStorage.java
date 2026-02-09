package com.glodblock.github.appflux.mixins;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "appeng.me.service.helpers.CraftingServiceStorage$1")
public class MixinCraftingServiceStorage {

    @Inject(
            method = "insert(Lappeng/api/stacks/AEKey;JLappeng/api/config/Actionable;Lappeng/api/networking/security/IActionSource;)J",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void skip(AEKey what, long amount, Actionable mode, IActionSource source, CallbackInfoReturnable<Long> cir) {
        if (what instanceof FluxKey) {
            cir.setReturnValue(0L);
            cir.cancel();
        }
    }

}
