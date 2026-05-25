package com.glodblock.github.extendedae.xmod.kjs;

import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.glodium.reflect.ReflectKit;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.BuilderFactory;
import dev.latvian.mods.kubejs.registry.BuilderTypeRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Method;

public class KJSPlugin implements KubeJSPlugin {

    private static Method devAdder;

    static {
        try {
            devAdder = ReflectKit.reflectMethod(BuilderTypeRegistry.Callback.class, "add", ResourceLocation.class, Class.class, BuilderFactory.class);
        } catch (Exception e) {
            devAdder = null;
        }
    }

    private <T> void invokeDevAdder(BuilderTypeRegistry.Callback<T> callback, ResourceLocation type, Class<? extends BuilderBase<? extends T>> builderType, BuilderFactory factory) {
        try {
            devAdder.invoke(callback, type, builderType, factory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void registerBuilderTypes(BuilderTypeRegistry registry) {
        if (devAdder != null) {
            registry.of(
                    Registries.ITEM,
                    r -> this.invokeDevAdder(r, ExtendedAE.id("custom_infinity_cell"), InfinityCellBuilder.class, InfinityCellBuilder::new)
            );
        } else {
            registry.of(
                    Registries.ITEM,
                    r -> r.add("custom_infinity_cell", InfinityCellBuilder.class, InfinityCellBuilder::new)
            );
        }
    }

}
