package com.glodblock.github.appflux;

import appeng.api.AECapabilities;
import com.glodblock.github.appflux.client.AFClientRegistryHandler;
import com.glodblock.github.appflux.common.AFSingletons;
import com.glodblock.github.appflux.common.AFRegistryHandler;
import com.glodblock.github.appflux.common.me.inventory.FEGenericStackInvStorage;
import com.glodblock.github.appflux.config.AFConfig;
import com.glodblock.github.appflux.network.AFNetworkHandler;
import com.glodblock.github.appflux.util.AFUtil;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;

@Mod(AppFlux.MODID)
public class AppFlux {

    public static final String MODID = "appflux";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static AppFlux INSTANCE;

    @SuppressWarnings("UnstableApiUsage")
    public AppFlux(IEventBus bus, ModContainer container) {
        assert INSTANCE == null;
        INSTANCE = this;
        if (!container.getModId().equals(MODID)) {
            throw new IllegalArgumentException("Invalid ID: " + MODID);
        }
        container.registerConfig(ModConfig.Type.COMMON, AFConfig.SPEC);
        AFRegistryHandler.INSTANCE = new AFRegistryHandler(bus);
        AFSingletons.init(AFRegistryHandler.INSTANCE);
        bus.addListener((RegisterEvent e) -> {
            if (e.getRegistryKey().equals(Registries.CREATIVE_MODE_TAB)) {
                AFRegistryHandler.INSTANCE.registerTab(e.getRegistry(Registries.CREATIVE_MODE_TAB));
            }
        });
        bus.addListener(this::commonSetup);
        bus.addListener(this::clientSetup);
        bus.addListener(AFNetworkHandler.INSTANCE::onRegister);
        bus.register(AFRegistryHandler.INSTANCE);
        if (FMLEnvironment.getDist().isClient()) {
            bus.register(AFClientRegistryHandler.INSTANCE);
        }
        bus.addListener(EventPriority.LOWEST, (RegisterCapabilitiesEvent event) -> {
            for (var block : BuiltInRegistries.BLOCK) {
                if (event.isBlockRegistered(AECapabilities.GENERIC_INTERNAL_INV, block)) {
                    event.registerBlock(
                            Capabilities.Energy.BLOCK,
                            (level, pos, state, tile, side) -> {
                                var genericInv = level.getCapability(AECapabilities.GENERIC_INTERNAL_INV, pos, state, tile, side);
                                if (genericInv != null && AFUtil.shouldTryCast(tile, side)) {
                                    return new FEGenericStackInvStorage(genericInv);
                                }
                                return null;
                            },
                            block
                    );
                }
            }
        });
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        AFRegistryHandler.INSTANCE.init();
    }

    public void clientSetup(FMLClientSetupEvent event) {
        AFClientRegistryHandler.INSTANCE.init();
    }

    public static Identifier id(String id) {
        return Identifier.fromNamespaceAndPath(MODID, id);
    }

    public static String stringId(String id) {
        return id(id).toString();
    }

}
