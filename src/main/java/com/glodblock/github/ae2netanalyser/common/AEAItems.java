package com.glodblock.github.ae2netanalyser.common;

import com.glodblock.github.ae2netanalyser.common.items.ItemNetworkAnalyzer;

public class AEAItems {

    public static ItemNetworkAnalyzer ANALYSER;

    public static void init(AEARegistryHandler regHandler) {
        ANALYSER = new ItemNetworkAnalyzer();
        regHandler.item("network_analyser", ANALYSER);
    }

}
