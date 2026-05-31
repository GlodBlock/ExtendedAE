package com.glodblock.github.extendedae.datagen;

import appeng.core.ConventionTags;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.EAESingletons;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class EAEComponentTagProvider extends IntrinsicHolderTagsProvider<@NotNull DataComponentType<?>> {
    public EAEComponentTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, Registries.DATA_COMPONENT_TYPE, registries, EAEComponentTagProvider::getKey, ExtendedAE.MODID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider registries) {
        tag(ConventionTags.EXPORTED_SETTINGS)
                .add(EAESingletons.MOD_EXPRESS.get())
                .add(EAESingletons.TAG_EXPRESS.get())
                .add(EAESingletons.EXTRA_SETTING.get())
                .add(EAESingletons.THRESHOLD_DATA.get())
                .add(EAESingletons.DIRECTION_SET.get());
    }

    private static ResourceKey<@NotNull DataComponentType<?>> getKey(DataComponentType<?> type) {
        return BuiltInRegistries.DATA_COMPONENT_TYPE.getResourceKey(type).orElseThrow();
    }

}
