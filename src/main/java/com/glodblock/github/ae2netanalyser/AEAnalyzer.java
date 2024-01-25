package com.glodblock.github.ae2netanalyser;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public abstract class AEAnalyzer {

    public static final String MODID = "ae2netanalyser";
    public static final Logger LOGGER = LogUtils.getLogger();

    public AEAnalyzer() {

    }

    public static ResourceLocation id(String id) {
        return new ResourceLocation(MODID, id);
    }

}
