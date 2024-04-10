package com.glodblock.github.extendedae.mixins;

import com.glodblock.github.extendedae.client.gui.GuiExPatternTerminal;
import com.glodblock.github.extendedae.client.gui.GuiWirelessExPAT;
import com.glodblock.github.extendedae.xmod.wt.GuiUWirelessExPAT;
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
        if (className.equals(GuiWirelessExPAT.class.getCanonicalName()) ||
            className.equals(GuiUWirelessExPAT.class.getCanonicalName()) ||
            className.equals(GuiExPatternTerminal.class.getCanonicalName())) {
            ci.cancel();
        }
    }

}
