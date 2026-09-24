package com.glodblock.github.appflux.util;

import com.glodblock.github.appflux.api.EnergyIO;

import java.util.function.Supplier;

public record IOSignal(Supplier<EnergyIO> config) {

    public static IOSignal of(Supplier<EnergyIO> config) {
        return new IOSignal(config);
    }

    public boolean isInput() {
        return this.config.get().isInput();
    }

    public boolean isOutput() {
        return this.config.get().isOutput();
    }

}
