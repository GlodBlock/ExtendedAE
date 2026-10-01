package com.glodblock.github.appflux.common.caps;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageService;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import com.glodblock.github.appflux.common.me.key.type.EnergyType;
import com.glodblock.github.appflux.util.AFUtil;
import com.glodblock.github.appflux.util.DeltaEnergyJournal;
import com.glodblock.github.appflux.util.IOSignal;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.NotNull;

public class NetworkFEPower implements EnergyHandler {

    private final IStorageService storage;
    private final IActionSource source;
    private final IOSignal signal;
    private final DeltaEnergyJournal journal;

    public NetworkFEPower(IStorageService storage, IActionSource source, IOSignal signal) {
        this.storage = storage;
        this.source = source;
        this.signal = signal;
        this.journal = new DeltaEnergyJournal(
                (amount, simulate) -> this.storage.getInventory().extract(FluxKey.of(EnergyType.FE), amount, AFUtil.ofSim(simulate), this.source),
                (amount, simulate) -> this.storage.getInventory().insert(FluxKey.of(EnergyType.FE), amount, AFUtil.ofSim(simulate), this.source)
        );
    }

    @Override
    public int insert(int amount, @NotNull TransactionContext transaction) {
        if (this.signal.isInput()) {
            TransferPreconditions.checkNonNegative(amount);
            this.journal.updateSnapshots(transaction);
            return (int) this.journal.onInsert(amount);
        } else {
            return 0;
        }
    }

    @Override
    public int extract(int amount, @NotNull TransactionContext transaction) {
        if (this.signal.isOutput()) {
            TransferPreconditions.checkNonNegative(amount);
            this.journal.updateSnapshots(transaction);
            return (int) this.journal.onExtract(amount);
        } else {
            return 0;
        }
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
