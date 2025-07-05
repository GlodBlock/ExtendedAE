package com.glodblock.github.appflux.common.me.cell;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.cells.CellState;
import appeng.api.storage.cells.StorageCell;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import com.glodblock.github.appflux.common.me.key.type.EnergyType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class FECreativeCellInventory implements StorageCell {

    private final ItemStack stack;

    public FECreativeCellInventory(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (what instanceof FluxKey) {
            return amount;
        }
        return 0;
    }

    @Override
    public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (what instanceof FluxKey) {
            return amount;
        }
        return 0;
    }

    @Override
    public void getAvailableStacks(KeyCounter out) {
        out.add(FluxKey.of(EnergyType.FE), Integer.MAX_VALUE);
    }

    @Override
    public boolean isPreferredStorageFor(AEKey input, IActionSource source) {
        return input instanceof FluxKey;
    }

    @Override
    public CellState getStatus() {
        return CellState.TYPES_FULL;
    }

    @Override
    public double getIdleDrain() {
        return 0;
    }

    @Override
    public void persist() {
        // NO-OP
    }

    @Override
    public Component getDescription() {
        return this.stack.getHoverName();
    }

}
