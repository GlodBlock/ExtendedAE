package com.glodblock.github.ae2netanalyser;

import appeng.init.client.InitScreens;
import com.glodblock.github.ae2netanalyser.client.gui.GuiAnalyser;
import com.glodblock.github.ae2netanalyser.client.render.NetworkRender;
import com.glodblock.github.ae2netanalyser.common.AEAItems;
import com.glodblock.github.ae2netanalyser.common.AEARegistryHandler;
import com.glodblock.github.ae2netanalyser.common.me.tracker.PlayerTracker;
import com.glodblock.github.ae2netanalyser.container.ContainerAnalyser;
import com.glodblock.github.ae2netanalyser.network.AEANetworkHandler;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;

@Mod(AEAnalyser.MODID)
public class AEAnalyser {

    public static final String MODID = "ae2netanalyser";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static AEAnalyser INSTANCE;

    public AEAnalyser(IEventBus bus) {
        assert INSTANCE == null;
        INSTANCE = this;
        PlayerTracker.init();
        if (FMLEnvironment.dist.isClient()) {
            NeoForge.EVENT_BUS.addListener(NetworkRender::hook);
        }
        bus.addListener(this::commonSetup);
        bus.addListener(this::clientSetup);
        bus.addListener((RegisterEvent e) -> {
            if (e.getRegistryKey() == Registries.CREATIVE_MODE_TAB) {
                AEARegistryHandler.INSTANCE.registerTab(e.getRegistry(Registries.CREATIVE_MODE_TAB));
                return;
            }
            if (e.getRegistryKey().equals(Registries.BLOCK)) {
                AEAItems.init(AEARegistryHandler.INSTANCE);
                AEARegistryHandler.INSTANCE.runRegister();
            }
        });
        bus.addListener(AEANetworkHandler.INSTANCE::onRegister);
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        AEARegistryHandler.INSTANCE.init();
    }

    public void clientSetup(FMLClientSetupEvent event) {
        InitScreens.register(ContainerAnalyser.TYPE, GuiAnalyser::new, "/screens/network_analyser.json");
    }

    public static ResourceLocation id(String id) {
        return new ResourceLocation(MODID, id);
    }

}
