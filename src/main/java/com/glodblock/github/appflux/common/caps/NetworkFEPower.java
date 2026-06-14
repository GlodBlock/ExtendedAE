package com.glodblock.github.appflux.common.caps;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageService;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import com.glodblock.github.appflux.common.me.key.type.EnergyType;
import com.glodblock.github.appflux.util.AFUtil;
import com.glodblock.github.appflux.util.DeltaEnergyJournal;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.NotNull;

public class NetworkFEPower implements EnergyHandler {

    private final IStorageService storage;
    private final IActionSource source;
    private final DeltaEnergyJournal journal;

    public NetworkFEPower(IStorageService storage, IActionSource source) {
        this.storage = storage;
        this.source = source;
        this.journal = new DeltaEnergyJournal(
                (amount, simulate) -> this.storage.getInventory().extract(FluxKey.of(EnergyType.FE), amount, AFUtil.ofSim(simulate), this.source),
                (amount, simulate) -> this.storage.getInventory().insert(FluxKey.of(EnergyType.FE), amount, AFUtil.ofSim(simulate), this.source)
        );
    }

    @Override
    public int insert(int amount, @NotNull TransactionContext transaction) {
        TransferPreconditions.checkNonNegative(amount);
        this.journal.updateSnapshots(transaction);
        return (int) this.journal.onInsert(amount);
    }

    @Override
    public int extract(int amount, @NotNull TransactionContext transaction) {
        TransferPreconditions.checkNonNegative(amount);
        this.journal.updateSnapshots(transaction);
        return (int) this.journal.onExtract(amount);
    }

    @Override
    public long getAmountAsLong() {
        return this.storage.getCachedInventory().get(FluxKey.of(EnergyType.FE));
    }

    @Override
    public long getCapacityAsLong() {
        var space = this.storage.getInventory().insert(FluxKey.of(EnergyType.FE), Long.MAX_VALUE - 1, Actionable.SIMULATE, this.source);
        return space + this.getAmountAsLong();
    }

}
