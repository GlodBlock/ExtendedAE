package com.glodblock.github.appflux.mixins;

import appeng.api.config.Actionable;
import appeng.blockentity.networking.CreativeEnergyCellBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(CreativeEnergyCellBlockEntity.class)
public abstract class MixinCreativeEnergyCellBlockEntity {

    /**
     * @author GlodBlock
     * @reason Make creative cell not an energy trash can
     */
    @Overwrite(remap = false)
    public double injectAEPower(double amt, Actionable mode) {
        return amt;
    }

}