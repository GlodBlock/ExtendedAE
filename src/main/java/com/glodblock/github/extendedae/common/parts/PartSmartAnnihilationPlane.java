package com.glodblock.github.extendedae.common.parts;

import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartItem;
import appeng.helpers.IConfigInvHost;
import appeng.helpers.externalstorage.GenericStackInv;
import appeng.parts.AEBasePart;

public class PartSmartAnnihilationPlane extends AEBasePart implements IGridTickable, IConfigInvHost {

    public PartSmartAnnihilationPlane(IPartItem<?> partItem) {
        super(partItem);
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        return null;
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        return null;
    }

    @Override
    public void getBoxes(IPartCollisionHelper bch) {

    }

    @Override
    public GenericStackInv getConfig() {
        return null;
    }
}
