package com.glodblock.github.ae2netanalyser.mixins;

import appeng.api.networking.ticking.TickRateModulation;
import appeng.me.service.TickManagerService;
import appeng.me.service.helpers.TickTracker;
import com.glodblock.github.ae2netanalyser.common.me.ticker.RequestBox;
import com.google.common.base.Stopwatch;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.TimeUnit;

@Mixin(TickManagerService.class)
public abstract class MixinTickManagerService {

    @Shadow
    @Final
    private Stopwatch stopWatch;

    @Redirect(
            method = "unsafeTickingRequest",
            at = @At(
                    value = "FIELD",
                    target = "Lappeng/me/service/TickManagerService;MONITORING_ENABLED:Z"
            )
    )
    private boolean needProfiler() {
        return TickManagerService.MONITORING_ENABLED || RequestBox.hasJob();
    }

    @Inject(
            method = "unsafeTickingRequest",
            at = @At(
                    value = "INVOKE",
                    target = "Lappeng/me/service/helpers/TickTracker;getStatistics()Ljava/util/LongSummaryStatistics;"
            )
    )
    private void captureTick(TickTracker tt, int diff, CallbackInfoReturnable<TickRateModulation> cir) {
        RequestBox.acceptTick(this.stopWatch.elapsed(TimeUnit.NANOSECONDS), diff, tt.getNode());
    }

}
