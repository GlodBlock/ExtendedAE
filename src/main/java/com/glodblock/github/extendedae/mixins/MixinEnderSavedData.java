package com.glodblock.github.extendedae.mixins;

import com.buuz135.functionalstorage.world.EnderSavedData;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderSavedData.class)
public abstract class MixinEnderSavedData {

    @Unique
    @Nullable
    private static final Class<?> GUIDE_WORLD = initGuideWorldClass();

    @Inject(
            method = "getInstance",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private static void clientWorld(LevelAccessor accessor, CallbackInfoReturnable<EnderSavedData> cir) {
        if (GUIDE_WORLD != null && GUIDE_WORLD.isInstance(accessor)) {
            cir.setReturnValue(EnderSavedData.CLIENT);
            cir.cancel();
        }
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Unique
    private static Class<?> initGuideWorldClass() {
        try {
            return Class.forName("guideme.scene.level.GuidebookLevel");
        } catch (Throwable ignored) {
            return null;
        }
    }

}
