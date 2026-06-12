package com.glodblock.github.extendedae.xmod.aae;

import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.client.gui.pattern.GuiProcessingPattern;
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
import net.pedroksl.advanced_ae.common.patterns.AdvProcessingPattern;

@ThirdParty(ModConstants.ADV_AE)
public class AAELoader implements XModLoader {

    @Override
    public String modid() {
        return ModConstants.ADV_AE;
    }

    @Override
    public void loadCommon() {
        PatternGuiHandler.addPatternHandler(AdvProcessingPattern.class, ContainerAdvProcessingPattern.ID);
    }

    @Override
    public void loadClient() {
        // NO-OP
    }

    @Override
    public void onRegister(RegistryHandler handler) {
        if (FMLEnvironment.getDist().isClient()) {
            ExtendedAE.MOD_BUS.addListener(this::bindGui);
        }
        ExtendedAE.MOD_BUS.addListener(this::registerContainer);
    }

    public void bindGui(RegisterMenuScreensEvent event) {
        event.register(ContainerAdvProcessingPattern.TYPE, GuiProcessingPattern::new);
    }

    public void registerContainer(RegisterEvent e) {
        if (e.getRegistry().equals(BuiltInRegistries.MENU)) {
            Registry.register(BuiltInRegistries.MENU, ContainerAdvProcessingPattern.ID, ContainerAdvProcessingPattern.TYPE);
        }
    }

}
