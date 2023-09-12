package com.github.glodblock.epp.common.me.wireless;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public enum WirelessStatus {

    UNCONNECTED,
    WORKING,
    REMOTE_ERROR,
    NO_POWER;

    public MutableComponent getTranslation() {
        return Component.translatable("gui.wireless_connect.status." + this.name().toLowerCase());
    }

    public MutableComponent getDesc() {
        return Component.translatable("gui.wireless_connect.status."  + this.name().toLowerCase() + ".desc");
    }

}
