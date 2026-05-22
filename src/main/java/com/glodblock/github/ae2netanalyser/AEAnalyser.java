package com.glodblock.github.ae2netanalyser;

import appeng.client.InitScreens;
import com.glodblock.github.ae2netanalyser.client.gui.GuiAnalyser;
import com.glodblock.github.ae2netanalyser.client.gui.GuiProfiler;
import com.glodblock.github.ae2netanalyser.client.render.NetworkRender;
import com.glodblock.github.ae2netanalyser.client.render.ProfileRender;
import com.glodblock.github.ae2netanalyser.common.AEASingletons;
import com.glodblock.github.ae2netanalyser.common.AEARegistryHandler;
import com.glodblock.github.ae2netanalyser.common.me.ticker.RequestBox;
import com.glodblock.github.ae2netanalyser.common.me.tracker.PlayerTracker;
import com.glodblock.github.ae2netanalyser.container.ContainerAnalyser;
import com.glodblock.github.ae2netanalyser.container.ContainerProfiler;
import com.glodblock.github.ae2netanalyser.network.AEANetworkHandler;
import com.glodblock.github.glodium.Glodium;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
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
        AEARegistryHandler.INSTANCE = new AEARegistryHandler(bus);
        AEASingletons.init(AEARegistryHandler.INSTANCE);
        PlayerTracker.init();
        RequestBox.init();
        if (FMLEnvironment.getDist().isClient()) {
            NeoForge.EVENT_BUS.addListener(NetworkRender::hook);
            NeoForge.EVENT_BUS.addListener(ProfileRender::hook);
        }
        bus.addListener(this::commonSetup);
        bus.addListener(this::guiRegister);
        bus.addListener((RegisterEvent e) -> {
            if (e.getRegistryKey() == Registries.CREATIVE_MODE_TAB) {
                AEARegistryHandler.INSTANCE.registerTab(e.getRegistry(Registries.CREATIVE_MODE_TAB));
            }
        });
        bus.addListener(AEANetworkHandler.INSTANCE::onRegister);
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        AEARegistryHandler.INSTANCE.init();
    }

    public void guiRegister(RegisterMenuScreensEvent event) {
        InitScreens.register(event, ContainerAnalyser.TYPE, GuiAnalyser::new, "/screens/network_analyser.json");
        InitScreens.register(event, ContainerProfiler.TYPE, GuiProfiler::new, "/screens/tick_analyser.json");
    }

    public static Identifier id(String id) {
        return Glodium.id(MODID, id);
    }

    public static String stringId(String id) {
        return id(id).toString();
    }

}
