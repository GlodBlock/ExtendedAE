package com.glodblock.github.ae2netanalyser.common;

import com.glodblock.github.ae2netanalyser.common.items.ItemNetworkAnalyzer;
import com.glodblock.github.ae2netanalyser.common.items.ItemTickAnalyzer;
import com.glodblock.github.glodium.util.GlodUtil;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponentType;

public class AEASingletons {

    public static DataComponentType<ItemNetworkAnalyzer.AnalyserConfig> ANALYZER_CONFIG;
    public static DataComponentType<ItemTickAnalyzer.TickConfig> TICK_CONFIG;
    public static DataComponentType<GlobalPos> GLOBAL_POS;

    public static ItemNetworkAnalyzer ANALYSER;
    public static ItemTickAnalyzer TICK_ANALYSER;

    public static void init(AEARegistryHandler regHandler) {
        ANALYZER_CONFIG = GlodUtil.getComponentType(ItemNetworkAnalyzer.AnalyserConfig.CODEC, ItemNetworkAnalyzer.AnalyserConfig.STREAM_CODEC);
        TICK_CONFIG = GlodUtil.getComponentType(ItemTickAnalyzer.TickConfig.CODEC, ItemTickAnalyzer.TickConfig.STREAM_CODEC);
        GLOBAL_POS = GlodUtil.getComponentType(GlobalPos.CODEC, GlobalPos.STREAM_CODEC);
        ANALYSER = new ItemNetworkAnalyzer();
        TICK_ANALYSER = new ItemTickAnalyzer();
        regHandler.comp("analyzer_config", ANALYZER_CONFIG);
        regHandler.comp("tick_config", TICK_CONFIG);
        regHandler.comp("global_pos", GLOBAL_POS);
        regHandler.item("network_analyser", ANALYSER);
        regHandler.item("tick_analyser", TICK_ANALYSER);
    }

}
