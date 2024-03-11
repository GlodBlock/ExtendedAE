package com.glodblock.github.appflux.common.me.inventory;

import appeng.api.behaviors.GenericInternalInventory;
import appeng.api.config.Actionable;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import com.glodblock.github.appflux.common.me.key.type.EnergyType;
import com.glodblock.github.appflux.util.AFUtil;
import net.neoforged.neoforge.energy.IEnergyStorage;

@SuppressWarnings("UnstableApiUsage")
public class FEGenericStackInvStorage implements IEnergyStorage {

    private final GenericInternalInventory inv;

    public FEGenericStackInvStorage(GenericInternalInventory inv) {
        this.inv = inv;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        long inserted = 0;
        long left = maxReceive;
        var mode = Actionable.ofSimulate(simulate);
        for (int slot = 0; slot < this.inv.size(); slot ++) {
            var in = this.inv.insert(slot, FluxKey.of(EnergyType.FE), left, mode);
            inserted += in;
            left -= in;
            if (left <= 0) {
                break;
            }
        }
        return (int) inserted;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        long extracted = 0;
        long left = maxExtract;
        var mode = Actionable.ofSimulate(simulate);
        for (int slot = 0; slot < this.inv.size(); slot ++) {
            var out = this.inv.extract(slot, FluxKey.of(EnergyType.FE), left, mode);
            extracted += out;
            left -= out;
            if (left <= 0) {
                break;
            }
        }
        return (int) extracted;
    }

    @Override
    public int getEnergyStored() {
        long cnt = 0;
        for (int slot = 0; slot < this.inv.size(); slot ++) {
            var stack = this.inv.getStack(slot);
            if (stack != null) {
                if (FluxKey.of(EnergyType.FE).equals(stack.what())) {
                    cnt += stack.amount();
                    if (cnt > Integer.MAX_VALUE) {
                        break;
                    }
                }
            }
        }
        return AFUtil.clampLong(cnt);
    }

    @Override
    public int getMaxEnergyStored() {
        long cnt = 0;
        for (int slot = 0; slot < this.inv.size(); slot ++) {
            var stack = this.inv.getStack(slot);
            if (stack != null) {
                if (FluxKey.of(EnergyType.FE).equals(stack.what())) {
                    cnt ++;
                }
            } else {
                cnt ++;
            }
        }
        return AFUtil.clampLong(cnt * this.inv.getMaxAmount(FluxKey.of(EnergyType.FE)));
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public boolean canReceive() {
        return true;
    }

}
