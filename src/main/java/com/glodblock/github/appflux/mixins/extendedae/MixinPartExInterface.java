package com.glodblock.github.appflux.mixins.extendedae;

import appeng.api.parts.IPartItem;
import appeng.helpers.InterfaceLogicHost;
import appeng.parts.AEBasePart;
import com.glodblock.github.appflux.util.AFUtil;
import com.glodblock.github.appflux.util.helpers.INeighborListener;
import com.glodblock.github.extendedae.common.parts.PartExInterface;
import com.glodblock.github.extendedae.common.parts.PartOversizeInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({PartExInterface.class, PartOversizeInterface.class})
public abstract class MixinPartExInterface extends AEBasePart implements InterfaceLogicHost {

    @Override
    public void onNeighborChanged(BlockGetter level, BlockPos pos, BlockPos neighbor) {
        var d = AFUtil.getBlockDirection(pos, neighbor);
        if (d == this.getSide() && d != null) {
            var listener = (INeighborListener) this.getInterfaceLogic();
            listener.onChange(d);
        }
    }

    public MixinPartExInterface(IPartItem<?> partItem) {
        super(partItem);
    }

}
