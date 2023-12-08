package com.github.glodblock.extendedae;

import com.github.glodblock.extendedae.common.EAEItemAndBlock;
import com.github.glodblock.extendedae.common.RegistryHandler;
import com.github.glodblock.extendedae.config.EPPConfig;
import com.github.glodblock.extendedae.network.EAENetworkServer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

public class EAEServer extends EAE implements ModInitializer {

    private static MinecraftServer server = null;

    @Override
    public void onInitialize() {
        EPPConfig.onInit();
        ServerLifecycleEvents.SERVER_STARTING.register(EAEServer::hookServer);
        EAEItemAndBlock.init(RegistryHandler.INSTANCE);
        RegistryHandler.INSTANCE.runRegister();
        RegistryHandler.INSTANCE.onInit();
        new EAENetworkServer();
    }

    private static void hookServer(MinecraftServer run) {
        server = run;
    }

    @Nullable
    public static MinecraftServer getServer() {
        return server;
    }

}
