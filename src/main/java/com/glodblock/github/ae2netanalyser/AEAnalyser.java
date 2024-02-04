package com.glodblock.github.ae2netanalyser;

import appeng.init.client.InitScreens;
import com.glodblock.github.ae2netanalyser.client.gui.GuiAnalyser;
import com.glodblock.github.ae2netanalyser.client.render.NetworkRender;
import com.glodblock.github.ae2netanalyser.common.AEAItems;
import com.glodblock.github.ae2netanalyser.common.AEARegistryHandler;
import com.glodblock.github.ae2netanalyser.common.me.network.tracker.PlayerTracker;
import com.glodblock.github.ae2netanalyser.container.ContainerAnalyser;
import com.glodblock.github.ae2netanalyser.network.AEANetworkHandler;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import org.slf4j.Logger;

@Mod(AEAnalyser.MODID)
public class AEAnalyser {

    public static final String MODID = "ae2netanalyser";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static AEAnalyser INSTANCE;

    public AEAnalyser() {
        assert INSTANCE == null;
        INSTANCE = this;
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        AEAItems.init(AEARegistryHandler.INSTANCE);
        bus.register(AEARegistryHandler.INSTANCE);
        PlayerTracker.init();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> MinecraftForge.EVENT_BUS.addListener(NetworkRender::hook));
        bus.addListener(this::commonSetup);
        bus.addListener(this::clientSetup);
        bus.addListener((RegisterEvent e) -> {
            if (e.getRegistryKey() == Registries.CREATIVE_MODE_TAB) {
                AEARegistryHandler.INSTANCE.registerTab(e.getVanillaRegistry());
            }
        });
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        AEARegistryHandler.INSTANCE.init();
        AEANetworkHandler.INSTANCE.init();
    }

    public void clientSetup(FMLClientSetupEvent event) {
        InitScreens.register(ContainerAnalyser.TYPE, GuiAnalyser::new, "/screens/network_analyser.json");
    }

    public static ResourceLocation id(String id) {
        return new ResourceLocation(MODID, id);
    }

}
