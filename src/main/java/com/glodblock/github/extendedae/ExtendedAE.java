package com.glodblock.github.extendedae;

import com.glodblock.github.extendedae.client.ClientRegistryHandler;
import com.glodblock.github.extendedae.client.hotkey.PatternHotKey;
import com.glodblock.github.extendedae.common.EAERegistryHandler;
import com.glodblock.github.extendedae.common.EAEItemAndBlock;
import com.glodblock.github.extendedae.common.hooks.CutterHook;
import com.glodblock.github.extendedae.config.EAEConfig;
import com.glodblock.github.extendedae.network.EAENetworkHandler;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModInfo;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;

@Mod(ExtendedAE.MODID)
public class ExtendedAE {

    public static final String MODID = "extendedae";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static ExtendedAE INSTANCE;

    public ExtendedAE(IEventBus bus) {
        assert INSTANCE == null;
        INSTANCE = this;
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, EAEConfig.SPEC);
        bus.addListener((RegisterEvent e) -> {
            if (e.getRegistryKey().equals(Registries.CREATIVE_MODE_TAB)) {
                EAERegistryHandler.INSTANCE.registerTab(e.getRegistry(Registries.CREATIVE_MODE_TAB));
                return;
            }
            if (e.getRegistryKey().equals(Registries.BLOCK)) {
                EAEItemAndBlock.init(EAERegistryHandler.INSTANCE);
                EAERegistryHandler.INSTANCE.runRegister();
            }
        });
        if (FMLEnvironment.dist.isClient()) {
            bus.register(ClientRegistryHandler.INSTANCE);
        }
        bus.addListener(this::commonSetup);
        bus.addListener(this::clientSetup);
        bus.addListener(EAENetworkHandler.INSTANCE::onRegister);
        bus.register(EAERegistryHandler.INSTANCE);
        NeoForge.EVENT_BUS.register(CutterHook.INSTANCE);
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        EAERegistryHandler.INSTANCE.onInit();
    }

    public void clientSetup(FMLClientSetupEvent event) {
        ClientRegistryHandler.INSTANCE.onInit();
        PatternHotKey.onInit();
    }

    public static boolean isLoad(String modid) {
        if (ModList.get() == null) {
            return LoadingModList.get().getMods()
                    .stream().map(ModInfo::getModId)
                    .anyMatch(modid::equals);
        } else {
            return ModList.get().isLoaded(modid);
        }
    }

    public static ResourceLocation id(String id) {
        return new ResourceLocation(MODID, id);
    }

}
