package com.glodblock.github.ae2netanalyser.common;

import com.glodblock.github.ae2netanalyser.common.items.ItemNetworkAnalyser;

public class AEAItems {

    public static ItemNetworkAnalyser ANALYSER;

    public static void init(AEARegistryHandler regHandler) {
        ANALYSER = new ItemNetworkAnalyser();
        regHandler.item("network_analyser", ANALYSER);
    }

}
