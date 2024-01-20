package com.glodblock.github.appflux;

import appeng.capabilities.Capabilities;
import com.glodblock.github.appflux.client.AFClientRegistryHandler;
import com.glodblock.github.appflux.common.AFItemAndBlock;
import com.glodblock.github.appflux.common.AFRegistryHandler;
import com.glodblock.github.appflux.common.me.inventory.FEGenericStackInvStorage;
import com.mojang.logging.LogUtils;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Mod(AppFlux.MODID)
public class AppFlux {

    public static final String MODID = "appflux";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static AppFlux INSTANCE;

    @SuppressWarnings("UnstableApiUsage")
    public AppFlux() {
        assert INSTANCE == null;
        INSTANCE = this;
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        AFItemAndBlock.init(AFRegistryHandler.INSTANCE);
        bus.register(AFRegistryHandler.INSTANCE);
        bus.addListener(this::commonSetup);
        bus.addListener(this::clientSetup);
        bus.addListener((RegisterEvent e) -> {
            if (e.getRegistryKey() == Registries.CREATIVE_MODE_TAB) {
                AFRegistryHandler.INSTANCE.registerTab(e.getVanillaRegistry());
            }
        });
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> bus.register(AFClientRegistryHandler.INSTANCE));
        MinecraftForge.EVENT_BUS.addGenericListener(BlockEntity.class, (AttachCapabilitiesEvent<BlockEntity> event) -> {
            var blockEntity = event.getObject();
            event.addCapability(AppFlux.id("generic_inv_wrapper"), new ICapabilityProvider() {
                @NotNull
                @Override
                public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
                    if (capability == ForgeCapabilities.ENERGY) {
                        return blockEntity.getCapability(Capabilities.GENERIC_INTERNAL_INV, side)
                                .lazyMap(FEGenericStackInvStorage::new)
                                .cast();
                    }
                    return LazyOptional.empty();
                }
            });
        });
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        AFRegistryHandler.INSTANCE.init();
    }

    public void clientSetup(FMLClientSetupEvent event) {
        AFClientRegistryHandler.INSTANCE.init();
    }

    public static ResourceLocation id(String id) {
        return new ResourceLocation(MODID, id);
    }

}
