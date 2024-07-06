package com.glodblock.github.extendedae.mixins;

import appeng.parts.automation.AbstractLevelEmitterPart;
import com.glodblock.github.extendedae.common.parts.PartThresholdLevelEmitter;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractLevelEmitterPart.class)
public abstract class MixinAbstractLevelEmitterPart {

    @Unique
    private static final DustParticleOptions eae$PURPLE = new DustParticleOptions(Vec3.fromRGB24(0xcc33cc).toVector3f(), 1f);

    @Redirect(
            method = "animateTick",
            at = @At(value = "FIELD", target = "net/minecraft/core/particles/DustParticleOptions.REDSTONE : Lnet/minecraft/core/particles/DustParticleOptions;")
    )
    private DustParticleOptions getParticle() {
        if (((Object) this) instanceof PartThresholdLevelEmitter) {
            return eae$PURPLE;
        }
        return DustParticleOptions.REDSTONE;
    }

}
