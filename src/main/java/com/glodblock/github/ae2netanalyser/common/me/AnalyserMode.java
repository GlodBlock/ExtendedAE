package com.glodblock.github.ae2netanalyser.common.me;

import net.minecraft.network.chat.Component;

public enum AnalyserMode {

    FULL, NODES, CHANNELS, NONUM, P2P;

    public static AnalyserMode byIndex(int index) {
        return AnalyserMode.values()[index];
    }

    public Component getTranslatedName() {
        return Component.translatable("gui.ae2netanalyser.network_analyser.mode." + this.name());
    }

}
