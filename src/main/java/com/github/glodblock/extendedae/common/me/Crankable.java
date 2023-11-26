package com.github.glodblock.extendedae.common.me;

import appeng.api.config.Actionable;
import appeng.api.config.PowerUnits;
import appeng.api.implementations.blockentities.ICrankable;
import appeng.blockentity.misc.CrankBlockEntity;
import appeng.blockentity.powersink.AEBasePoweredBlockEntity;

public class Crankable implements ICrankable {

    private final AEBasePoweredBlockEntity host;

    public Crankable(AEBasePoweredBlockEntity host) {
        this.host = host;
    }

    @Override
    public boolean canTurn() {
        return this.host.getInternalCurrentPower() < this.host.getInternalMaxPower();
    }

    @Override
    public void applyTurn() {
        this.host.injectExternalPower(PowerUnits.AE, CrankBlockEntity.POWER_PER_CRANK_TURN, Actionable.MODULATE);
    }
}