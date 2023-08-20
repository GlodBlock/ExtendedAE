package com.glodblock.github.epp.common.me;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.util.math.random.Random;

public class FreqGenerator {

    private final LongSet USED = new LongOpenHashSet();
    private final Random R = Random.create();

    public long genFreq() {
        var f = R.nextLong();
        while (this.USED.contains(f) || f == 0) {
            f = R.nextLong();
        }
        this.USED.add(f);
        return f;
    }

    public void put(long freq) {
        if (freq != 0) {
            this.USED.add(freq);
        }
    }

}
