package com.glodblock.github.appflux.util;

import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;

public class DeltaEnergyJournal extends SnapshotJournal<Long> {

    private final Extract extractor;
    private final Insert inserter;
    private long delta;

    public DeltaEnergyJournal(Extract extractor, Insert inserter) {
        this.extractor = extractor;
        this.inserter = inserter;
    }

    public long onInsert(long amount) {
        var newDelta = this.delta + amount;
        if (newDelta > 0) {
            var added = this.inserter.insert(newDelta, true);
            if (added < newDelta) {
                var actual = amount - (newDelta - added);
                this.delta = added;
                return actual;
            }
        }
        this.delta = newDelta;
        return amount;
    }

    public long onExtract(long amount) {
        var newDelta = this.delta - amount;
        if (newDelta < 0) {
            final var absDelta = -newDelta;
            var removed = this.extractor.extract(absDelta, true);
            if (removed < absDelta) {
                var actual = amount - (absDelta - removed);
                this.delta = removed;
                return actual;
            }
        }
        this.delta = newDelta;
        return amount;
    }

    @Override
    protected Long createSnapshot() {
        return this.delta;
    }

    @Override
    protected void revertToSnapshot(Long snapshot) {
        this.delta = snapshot;
    }

    @Override
    protected void onRootCommit(Long originalState) {
        if (this.delta > 0) {
            this.inserter.insert(this.delta, false);
        } else if (this.delta < 0) {
            this.extractor.extract(-this.delta, false);
        }
        this.delta = 0;
    }

    public interface Extract {

        long extract(long amount, boolean simulate);

    }

    public interface Insert {

        long insert(long amount, boolean simulate);

    }

}
