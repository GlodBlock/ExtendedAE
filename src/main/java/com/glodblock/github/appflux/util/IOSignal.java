package com.glodblock.github.appflux.util;

import com.glodblock.github.appflux.api.EnergyIO;

public record IOSignal(EnergyIOProvider config) {

    public static IOSignal of(EnergyIOProvider config) {
        return new IOSignal(config);
    }

    public boolean isInput() {
        return this.config.get().isInput();
    }

    public boolean isOutput() {
        return this.config.get().isOutput();
    }

    public interface EnergyIOProvider {

        EnergyIO get();

    }

}
