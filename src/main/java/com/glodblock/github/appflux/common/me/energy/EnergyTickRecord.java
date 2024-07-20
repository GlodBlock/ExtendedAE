package com.glodblock.github.appflux.common.me.energy;

public class EnergyTickRecord {

    public static final int MIN_RATE = 1;
    public static final int MAX_RATE = 30;
    long lastSent = 0;
    int rate = 10;
    long nextTick = 0;

    public void sent(long sent) {
        if (sent == this.lastSent) {
            if (sent != 0) {
                if (this.rate > MIN_RATE) {
                    this.rate--;
                }
            } else {
                if (this.rate < MAX_RATE) {
                    this.rate++;
                }
            }
        } else if (sent > this.lastSent) {
            if (this.rate > MIN_RATE) {
                this.rate--;
            }
        } else {
            if (this.rate < MAX_RATE) {
                this.rate++;
            }
        }
        this.lastSent = sent;
    }

    public boolean needTick(long current) {
        if (current >= this.nextTick) {
            this.nextTick = current + this.rate;
            return true;
        }
        return false;
    }

}
