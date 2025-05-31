package com.glodblock.github.appflux.mixins;

import appeng.api.parts.IPartItem;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.parts.AEBasePart;
import appeng.parts.crafting.PatternProviderPart;
import com.glodblock.github.appflux.util.AFUtil;
import com.glodblock.github.appflux.util.helpers.INeighborListener;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PatternProviderPart.class)
public abstract class MixinPatternProviderPart extends AEBasePart {

    @Final
    @Shadow(remap = false)
    protected PatternProviderLogic logic;

    @Override
    public void onNeighborChanged(BlockGetter level, BlockPos pos, BlockPos neighbor) {
        var d = AFUtil.getBlockDirection(pos, neighbor);
        if (d == this.getSide() && d != null) {
            var listener = (INeighborListener) this.logic;
            listener.onChange(d);
        }
    }

    public MixinPatternProviderPart(IPartItem<?> partItem) {
        super(partItem);
    }

}
