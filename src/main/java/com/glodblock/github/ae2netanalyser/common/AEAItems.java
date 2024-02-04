package com.glodblock.github.ae2netanalyser.common;

import com.glodblock.github.ae2netanalyser.common.items.ItemNetworkAnalyzer;
import com.glodblock.github.ae2netanalyser.common.items.ItemP2PAnalyzer;

public class AEAItems {

    public static ItemNetworkAnalyzer ANALYSER;
    public static ItemP2PAnalyzer P2P_ANALYSER;

    public static void init(AEARegistryHandler regHandler) {
        ANALYSER = new ItemNetworkAnalyzer();
        P2P_ANALYSER = new ItemP2PAnalyzer();
        regHandler.item("network_analyser", ANALYSER);
        regHandler.item("p2p_analyser", P2P_ANALYSER);
    }

}
