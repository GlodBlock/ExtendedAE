package com.glodblock.github.appflux.common.caps;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import com.glodblock.github.appflux.common.me.cell.FluxCellInventory;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import com.glodblock.github.appflux.common.me.key.type.EnergyType;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.NotNull;

public class CellFEPower implements EnergyHandler {

    private final FluxCellInventory inv;
    private final SnapshotJournal<Long> journal;

    public CellFEPower(FluxCellInventory inv) {
        this.inv = inv;
        this.journal = inv.getJournal();
    }

    @Override
    public long getAmountAsLong() {
        return this.inv.getStoredEnergy();
    }

    @Override
    public long getCapacityAsLong() {
        return this.inv.getMaxEnergy();
    }

    @Override
    public int insert(int amount, @NotNull TransactionContext transaction) {
        TransferPreconditions.checkNonNegative(amount);
        this.journal.updateSnapshots(transaction);
        return (int) this.inv.insert(FluxKey.of(EnergyType.FE), amount, Actionable.MODULATE, IActionSource.empty());
    }

    @Override
    public int extract(int amount, @NotNull TransactionContext transaction) {
        TransferPreconditions.checkNonNegative(amount);
        this.journal.updateSnapshots(transaction);
        return (int) this.inv.extract(FluxKey.of(EnergyType.FE), amount, Actionable.MODULATE, IActionSource.empty());
    }

}
