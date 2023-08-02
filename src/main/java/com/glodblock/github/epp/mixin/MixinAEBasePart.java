package com.glodblock.github.epp.mixin;

import appeng.api.networking.IManagedGridNode;
import appeng.parts.AEBasePart;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AEBasePart.class)
public abstract class MixinAEBasePart {

    @Redirect(
            method = "readFromNBT",
            remap = false,
            at = @At(
                    value = "INVOKE",
                    target = "appeng/api/networking/IManagedGridNode.loadFromNBT(Lnet/minecraft/nbt/NbtCompound;)V"
            )
    )
    private void checkReloadNBT(IManagedGridNode instance, NbtCompound data) {
        if (!data.contains("exae_reload")) {
            instance.loadFromNBT(data);
        }
    }

}
