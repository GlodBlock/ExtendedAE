package com.github.glodblock.epp.common.me;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;

public interface FreqGenerator<T> {

    @NotNull T genFreq();

    void markUsed(@NotNull T freq);

    static FreqGenerator<Long> createLong() {
        return new LongFreqGen();
    }

    static FreqGenerator<Integer> createInt() {
        return new IntFreqGen();
    }

}

class LongFreqGen implements FreqGenerator<Long> {

    private final LongSet USED = new LongOpenHashSet();
    private final RandomSource R = RandomSource.create();

    @Override
    public @NotNull Long genFreq() {
        var f = R.nextLong();
        while (this.USED.contains(f) || f == 0) {
            f = R.nextLong();
        }
        this.USED.add(f);
        return f;
    }

    @Override
    public void markUsed(@NotNull Long freq) {
        long f = freq;
        if (f != 0) {
            this.USED.add(f);
        }
    }
}

class IntFreqGen implements FreqGenerator<Integer> {

    private final IntSet USED = new IntOpenHashSet();
    private final RandomSource R = RandomSource.create();

    @Override
    public @NotNull Integer genFreq() {
        var f = R.nextInt();
        while (this.USED.contains(f) || f == 0) {
            f = R.nextInt();
        }
        this.USED.add(f);
        return f;
    }

    @Override
    public void markUsed(@NotNull Integer freq) {
        int f = freq;
        if (f != 0) {
            this.USED.add(f);
        }
    }
}
