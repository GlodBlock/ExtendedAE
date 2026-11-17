package com.glodblock.github.extendedae.datagen;

import appeng.core.ConventionTags;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.EAESingletons;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class EAEComponentTagProvider extends TagsProvider<@NotNull DataComponentType<?>> {
    public EAEComponentTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, Registries.DATA_COMPONENT_TYPE, registries, ExtendedAE.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        tag(ConventionTags.EXPORTED_SETTINGS)
                .add(key(EAESingletons.MOD_EXPRESS.get()))
                .add(key(EAESingletons.TAG_EXPRESS.get()))
                .add(key(EAESingletons.EXTRA_SETTING.get()))
                .add(key(EAESingletons.THRESHOLD_DATA.get()))
                .add(key(EAESingletons.DIRECTION_SET.get()));
    }

    private static ResourceKey<DataComponentType<?>> key(DataComponentType<?> type) {
        return BuiltInRegistries.DATA_COMPONENT_TYPE.getResourceKey(type).orElseThrow();
    }
}
