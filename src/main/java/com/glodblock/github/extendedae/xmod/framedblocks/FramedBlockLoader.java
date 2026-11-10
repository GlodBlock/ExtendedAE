package com.glodblock.github.extendedae.xmod.framedblocks;

import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.container.pattern.PatternGuiHandler;
import com.glodblock.github.extendedae.xmod.ModConstants;
import com.glodblock.github.glodium.registry.RegistryHandler;
import com.glodblock.github.glodium.xmod.ThirdParty;
import com.glodblock.github.glodium.xmod.XModLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@ThirdParty(ModConstants.FRAMED_BLOCKS)
public class FramedBlockLoader implements XModLoader {

    private static Class<?> PATTERN_CLASS;

    @Override
    public String modid() {
        return ModConstants.FRAMED_BLOCKS;
    }

    @Override
    public void loadCommon() {
        try {
            if (PATTERN_CLASS == null) {
                PATTERN_CLASS = Class.forName("io.github.xfacthd.framedblocks.common.compat.ae2.FramingSawPatternDetails");
            }
            PatternGuiHandler.addPatternHandler(PATTERN_CLASS, ContainerFramingSawPattern.ID);
        } catch (Throwable ignored) {
            // NO-OP
        }
    }

    @Override
    public void loadClient() {

    }

    @Override
    public void onRegister(RegistryHandler registryHandler) {
        if (FMLEnvironment.getDist().isClient()) {
            ExtendedAE.MOD_BUS.addListener(this::bindGui);
        }
        ExtendedAE.MOD_BUS.addListener(this::registerContainer);
    }

    public void bindGui(RegisterMenuScreensEvent event) {
        event.register(ContainerFramingSawPattern.TYPE, GuiFramingSawPattern::new);
    }

    public void registerContainer(RegisterEvent e) {
        if (e.getRegistry().equals(BuiltInRegistries.MENU)) {
            Registry.register(BuiltInRegistries.MENU, ContainerFramingSawPattern.ID, ContainerFramingSawPattern.TYPE);
        }
    }

}
