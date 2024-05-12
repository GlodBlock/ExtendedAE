package com.glodblock.github.appflux.common.me.inventory;


import appeng.api.behaviors.GenericInternalInventory;
import appeng.api.upgrades.IUpgradeableObject;
import com.glodblock.github.appflux.common.AFItemAndBlock;

@SuppressWarnings("UnstableApiUsage")
public class FEControllableInvStorage extends FEGenericStackInvStorage {

    private final IUpgradeableObject object;

    public FEControllableInvStorage(GenericInternalInventory inv, IUpgradeableObject obj) {
        super(inv);
        this.object = obj;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (object.isUpgradedWith(AFItemAndBlock.INDUCTION_CARD)) {
            return super.receiveEnergy(maxReceive, simulate);
        }
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (object.isUpgradedWith(AFItemAndBlock.INDUCTION_CARD)) {
            return super.extractEnergy(maxExtract, simulate);
        }
        return 0;
    }

    @Override
    public int getEnergyStored() {
        if (object.isUpgradedWith(AFItemAndBlock.INDUCTION_CARD)) {
            return super.getEnergyStored();
        }
        return 0;
    }

    @Override
    public int getMaxEnergyStored() {
        if (object.isUpgradedWith(AFItemAndBlock.INDUCTION_CARD)) {
            return super.getMaxEnergyStored();
        }
        return 0;
    }

    @Override
    public boolean canExtract() {
        if (object.isUpgradedWith(AFItemAndBlock.INDUCTION_CARD)) {
            return super.canExtract();
        }
        return false;
    }

    @Override
    public boolean canReceive() {
        if (object.isUpgradedWith(AFItemAndBlock.INDUCTION_CARD)) {
            return super.canReceive();
        }
        return false;
    }

}
