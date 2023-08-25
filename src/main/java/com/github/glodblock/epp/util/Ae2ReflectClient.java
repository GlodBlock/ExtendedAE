package com.github.glodblock.epp.util;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class Ae2ReflectClient {

    private static final Field fLevelRenderer_renderBuffers;
    private static final Field fMultiBufferSource_builder;
    private static final Field fMultiBufferSource_fixedBuffers;
    private static final Constructor<?> cFakeForwardingServerLevel;

    static {
        try {
            fLevelRenderer_renderBuffers = Ae2Reflect.reflectField(LevelRenderer.class, "renderBuffers", "f_109464_");
            fMultiBufferSource_builder = Ae2Reflect.reflectField(MultiBufferSource.BufferSource.class, "builder", "f_109904_");
            fMultiBufferSource_fixedBuffers = Ae2Reflect.reflectField(MultiBufferSource.BufferSource.class, "fixedBuffers", "f_109905_");
            cFakeForwardingServerLevel = Class
                    .forName("appeng.client.guidebook.scene.element.FakeForwardingServerLevel")
                    .getDeclaredConstructor(LevelAccessor.class);
            cFakeForwardingServerLevel.setAccessible(true);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize AE2 reflection hacks!", e);
        }
    }

    public static RenderBuffers getRenderBuffers(LevelRenderer owner) {
        return Ae2Reflect.readField(owner, fLevelRenderer_renderBuffers);
    }

    public static BufferBuilder getBufferBuilder(MultiBufferSource.BufferSource owner) {
        return Ae2Reflect.readField(owner, fMultiBufferSource_builder);
    }

    public static Map<RenderType, BufferBuilder> getFixedBuffer(MultiBufferSource.BufferSource owner) {
        return Ae2Reflect.readField(owner, fMultiBufferSource_fixedBuffers);
    }

    public static ServerLevelAccessor getFakeServerWorld(LevelAccessor world) {
        try {
            return (ServerLevelAccessor) cFakeForwardingServerLevel.newInstance(world);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

}
