package com.github.glodblock.extendedae.common.me.wireless;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public enum WirelessFail {

    OUT_OF_RANGE,
    SELF_REFERENCE,
    CROSS_DIMENSION,
    MISSING;

    public Component getTranslation() {
        return Component.translatable("chat.wireless_connect." + this.name().toLowerCase()).withStyle(ChatFormatting.RED);
    }

}
