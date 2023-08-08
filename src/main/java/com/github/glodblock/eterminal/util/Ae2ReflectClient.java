package com.github.glodblock.eterminal.util;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderBuffers;

import java.lang.reflect.Field;

public class Ae2ReflectClient {

    private static final Field fLevelRenderer_renderBuffers;

    static {
        try {
            fLevelRenderer_renderBuffers = Ae2Reflect.reflectField(LevelRenderer.class, "renderBuffers", "f_109464_");
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize AE2 reflection hacks!", e);
        }
    }

    public static RenderBuffers getRenderBuffers(LevelRenderer owner) {
        return Ae2Reflect.readField(owner, fLevelRenderer_renderBuffers);
    }

}