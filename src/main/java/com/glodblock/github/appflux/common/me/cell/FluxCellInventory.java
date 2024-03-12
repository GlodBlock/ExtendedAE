package com.glodblock.github.appflux.common.me.cell;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.cells.CellState;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.core.definitions.AEItems;
import com.glodblock.github.appflux.api.IFluxCell;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import com.glodblock.github.appflux.common.me.key.type.EnergyType;
import com.glodblock.github.appflux.common.me.key.type.FluxKeyType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public abstract class FluxCellInventory implements StorageCell {

    protected static final String DATA = "power";
    protected final IFluxCell cellType;
    protected final ItemStack stack;
    protected final boolean hasVoidUpgrade;
    @Nullable
    protected final ISaveProvider container;

    protected long storedEnergy = 0;
    protected boolean isPersisted = true;

    public FluxCellInventory(IFluxCell cellType, ItemStack o, @Nullable ISaveProvider container) {
        this.cellType = cellType;
        this.stack = o;
        this.container = container;
        var tag = o.getTag();
        if (tag != null) {
            storedEnergy = tag.getLong(DATA);
        }
        this.hasVoidUpgrade = this.getUpgrades().isInstalled(AEItems.VOID_CARD);
    }

    @Override
    public CellState getStatus() {
        if (this.storedEnergy == 0) {
            return CellState.EMPTY;
        }
        if (this.storedEnergy == getMaxEnergy()) {
            return CellState.FULL;
        }
        return CellState.NOT_EMPTY;
    }

    public IUpgradeInventory getUpgrades() {
        return this.cellType.getUpgrades(this.stack);
    }

    @Override
    public double getIdleDrain() {
        return this.cellType.getIdleDrain();
    }

    public long getStoredEnergy() {
        return this.storedEnergy;
    }

    public long getMaxEnergy() {
        return this.cellType.getBytes(this.stack) * FluxKeyType.TYPE.getAmountPerByte();
    }

    public long getTotalBytes() {
        return this.cellType.getBytes(this.stack);
    }

    public long getUsedBytes() {
        long amountPerByte = FluxKeyType.TYPE.getAmountPerByte();
        return (this.storedEnergy + amountPerByte - 1) / amountPerByte;
    }

    protected void saveChanges() {
        this.isPersisted = false;
        if (this.container != null) {
            this.container.saveChanges();
        } else {
            this.persist();
        }
    }

    @Override
    public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (!(what instanceof FluxKey)) {
            return 0;
        }

        var inserted = Math.min(getMaxEnergy() - this.storedEnergy, amount);

        if (mode == Actionable.MODULATE) {
            this.storedEnergy += inserted;
            saveChanges();
        }

        return this.hasVoidUpgrade ? amount : inserted;
    }

    @Override
    public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (!(what instanceof FluxKey)) {
            return 0;
        }

        var extracted = Math.min(this.storedEnergy, amount);

        if (mode == Actionable.MODULATE) {
            this.storedEnergy -= extracted;
            saveChanges();
        }

        return extracted;
    }

    @Override
    public void persist() {
        if (this.isPersisted) {
            return;
        }
        if (this.storedEnergy <= 0) {
            this.stack.removeTagKey(DATA);
        } else {
            this.stack.getOrCreateTag().putLong(DATA, this.storedEnergy);
        }
        this.isPersisted = true;
    }

    @Override
    public void getAvailableStacks(KeyCounter out) {
        if (this.storedEnergy > 0) {
            out.add(FluxKey.of(getEnergyType()), this.storedEnergy);
        }
    }

    protected abstract EnergyType getEnergyType();

    @Override
    public Component getDescription() {
        return this.stack.getHoverName();
    }
}
