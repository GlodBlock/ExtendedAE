package com.glodblock.github.extendedae.xmod.kjs;

import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.registry.BuilderTypeRegistry;
import net.minecraft.core.registries.Registries;

public class KJSPlugin implements KubeJSPlugin {

    @Override
    public void registerBuilderTypes(BuilderTypeRegistry registry) {
        registry.of(
                Registries.ITEM,
                r -> r.add("custom_infinity_cell", InfinityCellBuilder.class, InfinityCellBuilder::new)
        );
    }

}
