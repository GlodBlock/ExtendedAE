package com.glodblock.github.appflux.mixins.extendedae;

import appeng.api.parts.IPartItem;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.parts.AEBasePart;
import com.glodblock.github.appflux.util.AFUtil;
import com.glodblock.github.appflux.util.helpers.INeighborListener;
import com.glodblock.github.extendedae.common.parts.PartExPatternProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PartExPatternProvider.class)
public abstract class MixinPartExPatternProvider extends AEBasePart implements PatternProviderLogicHost {

    @Inject(
            method = "onNeighborChanged",
            at = @At("HEAD"),
            remap = false
    )
    private void injectChange(BlockGetter level, BlockPos pos, BlockPos neighbor, CallbackInfo ci) {
        var d = AFUtil.getBlockDirection(pos, neighbor);
        if (d == this.getSide() && d != null) {
            var listener = (INeighborListener) this.getLogic();
            listener.onChange(d);
        }
    }

    public MixinPartExPatternProvider(IPartItem<?> partItem) {
        super(partItem);
    }

}
