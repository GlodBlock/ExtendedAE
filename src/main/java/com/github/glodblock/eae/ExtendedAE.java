package com.github.glodblock.eae;

import com.github.glodblock.eae.proxy.CommonProxy;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = ExtendedAE.MODID, useMetadata = true, dependencies = "required:appliedenergistics2")
public class ExtendedAE {

    public static final String MODID = "extendedae";

    @Mod.Instance(MODID)
    public static ExtendedAE INSTANCE;

    @SidedProxy(clientSide = "com.github.glodblock.eae.proxy.ClientProxy", serverSide = "com.github.glodblock.eae.proxy.CommonProxy")
    public static CommonProxy proxy;

    public static Logger log;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        log = event.getModLog();
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void onPostInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MODID, path);
    }

}
