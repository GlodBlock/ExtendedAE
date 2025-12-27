package com.glodblock.github.ae2netanalyser.common.me.ticker;

public class GridTickProfiler {

    private long nanoseconds = 0;
    private long ticks = 0;

    public void start() {
        this.nanoseconds = 0;
        this.ticks = 0;
    }

    public double rate() {
        if (this.nanoseconds == 0) {
            return 0;
        }
        return (double) this.ticks / this.nanoseconds;
    }

    public void update(long tick, long nanosecond) {
        this.ticks += tick;
        this.nanoseconds += nanosecond;
    }

}
