package com.github.glodblock.extendedae;

import com.mojang.logging.LogUtils;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class EAE {

    public static final String MODID = "extendedae";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ResourceLocation id(String id) {
        return new ResourceLocation(MODID, id);
    }

    public static boolean checkMod(String id) {
        return FabricLoaderImpl.INSTANCE.isModLoaded(id);
    }

}
