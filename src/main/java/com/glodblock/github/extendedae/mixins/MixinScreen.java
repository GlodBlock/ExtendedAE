package com.glodblock.github.extendedae.mixins;

import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class MixinScreen {

    @Inject(
            method = "wrapScreenError",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/CrashReport;forThrowable(Ljava/lang/Throwable;Ljava/lang/String;)Lnet/minecraft/CrashReport;"),
            cancellable = true
    )
    private static void ignoreCrash(Runnable task, String info, String className, CallbackInfo ci) {
        if (className.equals("com.glodblock.github.extendedae.client.gui.GuiWirelessExPAT") ||
            className.equals("com.glodblock.github.extendedae.xmod.wt.GuiUWirelessExPAT") ||
            className.equals("com.glodblock.github.extendedae.client.gui.GuiExPatternTerminal")) {
            ci.cancel();
        }
    }

}
