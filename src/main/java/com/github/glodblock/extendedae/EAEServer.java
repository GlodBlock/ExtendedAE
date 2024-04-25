package com.github.glodblock.extendedae;

import appeng.api.IAEAddonEntrypoint;
import com.github.glodblock.extendedae.common.EAEItemAndBlock;
import com.github.glodblock.extendedae.common.RegistryHandler;
import com.github.glodblock.extendedae.common.hooks.CutterHook;
import com.github.glodblock.extendedae.config.EPPConfig;
import com.github.glodblock.extendedae.network.EAENetworkServer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

public class EAEServer extends EAE implements IAEAddonEntrypoint {

    private static MinecraftServer server = null;

    @Override
    public void onAe2Initialized() {
        EPPConfig.onInit();
        ServerLifecycleEvents.SERVER_STARTING.register(EAEServer::hookServer);
        EAEItemAndBlock.init(RegistryHandler.INSTANCE);
        RegistryHandler.INSTANCE.runRegister();
        RegistryHandler.INSTANCE.onInit();
        UseBlockCallback.EVENT.register(CutterHook.INSTANCE::onPlayerUseBlock);
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
