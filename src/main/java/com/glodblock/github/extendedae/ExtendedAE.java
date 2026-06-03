package com.glodblock.github.extendedae;

import com.glodblock.github.extendedae.client.ClientRegistryHandler;
import com.glodblock.github.extendedae.client.hooks.TagHook;
import com.glodblock.github.extendedae.client.hotkey.PatternHotKey;
import com.glodblock.github.extendedae.common.EAERegistryHandler;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.common.hooks.CutterHook;
import com.glodblock.github.extendedae.config.EAEConfig;
import com.glodblock.github.extendedae.network.EAENetworkHandler;
import com.glodblock.github.extendedae.recipe.CircuitCutterRecipe;
import com.glodblock.github.extendedae.recipe.CrystalAssemblerRecipe;
import com.glodblock.github.extendedae.recipe.CrystalFixerRecipe;
import com.glodblock.github.extendedae.xmod.ModConstants;
import com.glodblock.github.extendedae.xmod.darkmode.BlacklistGUI;
import com.glodblock.github.glodium.Glodium;
import com.glodblock.github.glodium.util.GlodUtil;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;

@Mod(ExtendedAE.MODID)
public class ExtendedAE {

    public static final String MODID = "extendedae";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static ExtendedAE INSTANCE;

    public ExtendedAE(IEventBus bus, ModContainer container) {
        assert INSTANCE == null;
        INSTANCE = this;
        if (!container.getModId().equals(MODID)) {
            throw new IllegalArgumentException("Invalid ID: " + MODID);
        }
        EAERegistryHandler.INSTANCE = new EAERegistryHandler(bus);
        EAESingletons.init(EAERegistryHandler.INSTANCE);
        container.registerConfig(ModConfig.Type.COMMON, EAEConfig.SPEC);
        bus.addListener((RegisterEvent e) -> {
            if (e.getRegistryKey().equals(Registries.CREATIVE_MODE_TAB)) {
                EAERegistryHandler.INSTANCE.registerTab(e.getRegistry(Registries.CREATIVE_MODE_TAB));
            }
        });
        if (FMLEnvironment.getDist().isClient()) {
            bus.register(ClientRegistryHandler.INSTANCE);
        }
        bus.addListener(this::commonSetup);
        bus.addListener(this::clientSetup);
        bus.addListener(this::sendIMC);
        bus.addListener(EAENetworkHandler.INSTANCE::onRegister);
        bus.register(EAERegistryHandler.INSTANCE);
        NeoForge.EVENT_BUS.register(CutterHook.INSTANCE);
        NeoForge.EVENT_BUS.addListener(this::sendSyncRecipe);
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        EAERegistryHandler.INSTANCE.onInit();
    }

    public void clientSetup(FMLClientSetupEvent event) {
        PatternHotKey.onInit();
        TagHook.onInit();
        CutterHook.addTooltip();
    }

    public void sendIMC(InterModEnqueueEvent event) {
        if (GlodUtil.checkMod(ModConstants.DARK_MODE)) {
            for (var method : BlacklistGUI.LIST) {
                InterModComms.sendTo(ModConstants.DARK_MODE, "dme-shaderblacklist", () -> method);
            }
        }
    }

    public void sendSyncRecipe(OnDatapackSyncEvent event) {
        event.sendRecipes(CircuitCutterRecipe.TYPE, CrystalAssemblerRecipe.TYPE, CrystalFixerRecipe.TYPE);
    }

    public static Identifier id(String id) {
        return Glodium.id(MODID, id);
    }

    public static String stringId(String id) {
        return id(id).toString();
    }

}
