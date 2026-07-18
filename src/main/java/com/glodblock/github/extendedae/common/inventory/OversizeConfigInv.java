package com.glodblock.github.extendedae.common.inventory;

import appeng.api.behaviors.GenericSlotCapacities;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.storage.AEKeyFilter;
import appeng.helpers.externalstorage.GenericStackInv;
import appeng.util.ConfigInventory;
import com.glodblock.github.extendedae.config.EPPConfig;
import org.jetbrains.annotations.Nullable;

public class OversizeConfigInv extends ConfigInventory {

    private final boolean allowOverstacking;

    public OversizeConfigInv(@Nullable AEKeyFilter filter, GenericStackInv.Mode mode, int size, @Nullable Runnable listener, boolean allowOverstacking) {
        super(filter, mode, size, listener, allowOverstacking);
        this.allowOverstacking = allowOverstacking;
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void useRegisteredCapacities() {
        for (var entry : GenericSlotCapacities.getMap().entrySet()) {
            var cap = entry.getValue();
            var key = entry.getKey();
            try {
                cap = Math.multiplyExact(cap, EPPConfig.getOversizeMultiplier(key));
            } catch (Exception e) {
                cap = Long.MAX_VALUE;
            }
            this.setCapacity(key, cap);
        }
    }

    @Override
    public long getMaxAmount(AEKey key) {
        if (this.allowOverstacking)
            return this.getCapacity(key.getType());
        if (key instanceof AEItemKey itemKey) {
            return Math.min((long) itemKey.getMaxStackSize() * EPPConfig.getOversizeMultiplier(itemKey.getType()), getCapacity(key.getType()));
        }
        return this.getCapacity(key.getType());
    }

}