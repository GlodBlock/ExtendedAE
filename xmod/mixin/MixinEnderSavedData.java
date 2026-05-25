package com.glodblock.github.extendedae.mixins;

import com.buuz135.functionalstorage.world.EnderSavedData;
import guideme.scene.level.GuidebookLevel;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderSavedData.class)
public abstract class MixinEnderSavedData {

    @Inject(
            method = "getInstance",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private static void clientWorld(LevelAccessor accessor, CallbackInfoReturnable<EnderSavedData> cir) {
        if (accessor instanceof GuidebookLevel) {
            cir.setReturnValue(EnderSavedData.CLIENT);
            cir.cancel();
        }
    }

}
