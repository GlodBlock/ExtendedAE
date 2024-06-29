package com.glodblock.github.ae2netanalyser.common;

import com.glodblock.github.ae2netanalyser.common.items.ItemNetworkAnalyzer;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponentType;

public class AEAComponents {

    public static DataComponentType<ItemNetworkAnalyzer.AnalyserConfig> ANALYZER_CONFIG;
    public static DataComponentType<GlobalPos> GLOBAL_POS;

    public static void init(AEARegistryHandler regHandler) {
        ANALYZER_CONFIG = DataComponentType.<ItemNetworkAnalyzer.AnalyserConfig>builder()
                .persistent(ItemNetworkAnalyzer.AnalyserConfig.CODEC)
                .networkSynchronized(ItemNetworkAnalyzer.AnalyserConfig.STREAM_CODEC)
                .build();
        GLOBAL_POS = DataComponentType.<GlobalPos>builder()
                .persistent(GlobalPos.CODEC)
                .networkSynchronized(GlobalPos.STREAM_CODEC)
                .build();
        regHandler.comp("analyzer_config", ANALYZER_CONFIG);
        regHandler.comp("global_pos", GLOBAL_POS);
    }

}
