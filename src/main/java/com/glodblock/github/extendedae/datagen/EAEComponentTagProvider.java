package com.glodblock.github.extendedae.datagen;

import appeng.datagen.providers.tags.ConventionTags;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.EAESingletons;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class EAEComponentTagProvider extends TagsProvider<DataComponentType<?>> {
    public EAEComponentTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, Registries.DATA_COMPONENT_TYPE, registries, ExtendedAE.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider registries) {
        tag(ConventionTags.EXPORTED_SETTINGS)
                .add(getKey(EAESingletons.MOD_EXPRESS))
                .add(getKey(EAESingletons.TAG_EXPRESS))
                .add(getKey(EAESingletons.EXTRA_SETTING))
                .add(getKey(EAESingletons.THRESHOLD_DATA));
    }

    private ResourceKey<DataComponentType<?>> getKey(DataComponentType<?> type) {
        return BuiltInRegistries.DATA_COMPONENT_TYPE.getResourceKey(type).orElseThrow();
    }

}
