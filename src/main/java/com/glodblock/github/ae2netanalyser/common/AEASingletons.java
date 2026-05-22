package com.glodblock.github.ae2netanalyser.common;

import com.glodblock.github.ae2netanalyser.common.items.ItemNetworkAnalyzer;
import com.glodblock.github.ae2netanalyser.common.items.ItemTickAnalyzer;
import com.glodblock.github.glodium.registry.defer.DeferredDataComponentType;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.NotNull;

public class AEASingletons {

    public static DeferredDataComponentType<ItemNetworkAnalyzer.@NotNull AnalyserConfig> ANALYZER_CONFIG;
    public static DeferredDataComponentType<ItemTickAnalyzer.@NotNull TickConfig> TICK_CONFIG;
    public static DeferredDataComponentType<@NotNull GlobalPos> GLOBAL_POS;

    public static DeferredItem<@NotNull ItemNetworkAnalyzer> ANALYSER;
    public static DeferredItem<@NotNull ItemTickAnalyzer> TICK_ANALYSER;

    public static void init(AEARegistryHandler regHandler) {
        ANALYZER_CONFIG = regHandler.comp("analyzer_config", ItemNetworkAnalyzer.AnalyserConfig.CODEC, ItemNetworkAnalyzer.AnalyserConfig.STREAM_CODEC);
        TICK_CONFIG = regHandler.comp("tick_config", ItemTickAnalyzer.TickConfig.CODEC, ItemTickAnalyzer.TickConfig.STREAM_CODEC);
        GLOBAL_POS = regHandler.comp("global_pos", GlobalPos.CODEC, GlobalPos.STREAM_CODEC);
        ANALYSER = regHandler.item("network_analyser", ItemNetworkAnalyzer::new, new Item.Properties().stacksTo(1));
        TICK_ANALYSER = regHandler.item("tick_analyser", ItemTickAnalyzer::new, new Item.Properties().stacksTo(1));
    }

}
