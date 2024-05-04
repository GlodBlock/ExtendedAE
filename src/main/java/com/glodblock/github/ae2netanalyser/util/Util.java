package com.glodblock.github.ae2netanalyser.util;

import appeng.api.networking.pathing.ChannelMode;
import appeng.core.AEConfig;

public class Util {

    public static ChannelMode getChannelMode() {
        return AEConfig.instance().getChannelMode();
    }

    public static boolean isInfChannel() {
        return AEConfig.instance().getChannelMode() == ChannelMode.INFINITE;
    }

}
