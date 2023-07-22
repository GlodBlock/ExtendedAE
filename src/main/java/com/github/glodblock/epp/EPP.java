package com.github.glodblock.epp;

import com.github.glodblock.epp.client.ClientRegistryHandler;
import com.github.glodblock.epp.common.EPPItemAndBlock;
import com.github.glodblock.epp.common.RegistryHandler;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(EPP.MODID)
public class EPP {

    public static final String MODID = "expatternprovider";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static EPP INSTANCE;

    public EPP() {
        assert INSTANCE == null;
        INSTANCE = this;
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        EPPItemAndBlock.init(RegistryHandler.INSTANCE);
        bus.register(RegistryHandler.INSTANCE);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> bus.register(ClientRegistryHandler.INSTANCE));
        bus.addListener(this::commonSetup);
        bus.addListener(this::clientSetup);
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        RegistryHandler.INSTANCE.onInit();
    }

    public void clientSetup(FMLClientSetupEvent event) {
        ClientRegistryHandler.INSTANCE.init();
    }

    public static ResourceLocation id(String id) {
        return new ResourceLocation(MODID, id);
    }

}
