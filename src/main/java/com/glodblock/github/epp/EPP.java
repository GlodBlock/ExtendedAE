package com.glodblock.github.epp;

import com.glodblock.github.epp.common.hooks.WirelessConnectHook;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class EPP {

    public static final String MODID = "expatternprovider";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public static Identifier id(String id) {
        return new Identifier(MODID, id);
    }

    public void common() {
        UseBlockCallback.EVENT.register(WirelessConnectHook::onPlayerUseBlock);
    }

}
