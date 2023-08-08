package com.github.glodblock.eterminal;

import com.github.glodblock.eterminal.client.ClientRegistryHandler;
import com.github.glodblock.eterminal.common.ETerminalItemAndBlock;
import com.github.glodblock.eterminal.common.RegistryHandler;
import com.github.glodblock.eterminal.network.ETerminalNetworkHandler;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;

@Mod(EnhancedTerminal.MODID)
public class EnhancedTerminal {

    public static final String MODID = "eterminal";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static EnhancedTerminal INSTANCE;

    public EnhancedTerminal() {
        assert INSTANCE == null;
        INSTANCE = this;
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ETerminalItemAndBlock.init(RegistryHandler.INSTANCE);
        bus.register(RegistryHandler.INSTANCE);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> bus.register(ClientRegistryHandler.INSTANCE));
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> MinecraftForge.EVENT_BUS.addListener(ClientRegistryHandler.INSTANCE::registerHighLightRender));
        bus.addListener(this::commonSetup);
        bus.addListener(this::clientSetup);
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        RegistryHandler.INSTANCE.onInit();
        ETerminalNetworkHandler.INSTANCE.init();
    }

    public void clientSetup(FMLClientSetupEvent event) {
        ClientRegistryHandler.INSTANCE.init();
    }

    public static ResourceLocation id(String id) {
        return new ResourceLocation(MODID, id);
    }

    public MinecraftServer getServer() {
        return ServerLifecycleHooks.getCurrentServer();
    }

}
