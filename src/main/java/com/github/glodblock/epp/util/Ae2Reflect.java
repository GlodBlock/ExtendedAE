package com.github.glodblock.epp.util;

import appeng.helpers.patternprovider.PatternContainer;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public class Ae2Reflect {

    private static final Field fContainerTracker_serverId;
    private static final Field fContainerTracker_container;
    private static final Field fUseOnContext_hitResult;

    static {
        try {
            fContainerTracker_serverId = reflectField(Class.forName("appeng.menu.implementations.PatternAccessTermMenu$ContainerTracker"), "serverId");
            fContainerTracker_container = reflectField(Class.forName("appeng.menu.implementations.PatternAccessTermMenu$ContainerTracker"), "container");
            fUseOnContext_hitResult = reflectField(UseOnContext.class, "hitResult", "f_43705_");
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize AE2 reflection hacks!", e);
        }
    }

    public static Method reflectMethod(Class<?> owner, String name, Class<?>... paramTypes) throws NoSuchMethodException {
        return reflectMethod(owner, new String[]{name}, paramTypes);
    }

    @SuppressWarnings("all")
    public static Method reflectMethod(Class<?> owner, String[] names, Class<?>... paramTypes) throws NoSuchMethodException {
        Method m = null;
        for (String name : names) {
            try {
                m = owner.getDeclaredMethod(name, paramTypes);
                if (m != null) break;
            }
            catch (NoSuchMethodException ignore) {
            }
        }
        if (m == null) throw new NoSuchMethodException("Can't find field from " + Arrays.toString(names));
        m.setAccessible(true);
        return m;
    }

    @SuppressWarnings("all")
    public static Field reflectField(Class<?> owner, String ...names) throws NoSuchFieldException {
        Field f = null;
        for (String name : names) {
            try {
                f = owner.getDeclaredField(name);
                if (f != null) break;
            }
            catch (NoSuchFieldException ignore) {
            }
        }
        if (f == null) throw new NoSuchFieldException("Can't find field from " + Arrays.toString(names));
        f.setAccessible(true);
        return f;
    }

    @SuppressWarnings("unchecked")
    public static <T> T readField(Object owner, Field field) {
        try {
            return (T)field.get(owner);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to read field: " + field);
        }
    }

    public static void writeField(Object owner, Field field, Object value) {
        try {
            field.set(owner, value);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to write field: " + field);
        }
    }

    public static long getContainerID(Object owner) {
        return readField(owner, fContainerTracker_serverId);
    }

    public static PatternContainer getContainer(Object owner) {
        return readField(owner, fContainerTracker_container);
    }

    public static BlockHitResult getHitResult(UseOnContext owner) {
        return readField(owner, fUseOnContext_hitResult);
    }

}
