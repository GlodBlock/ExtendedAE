package com.glodblock.github.appflux.datagen;

import com.glodblock.github.appflux.common.AFSingletons;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class AFLootTableProvider extends LootTableProvider {

    public AFLootTableProvider(PackOutput p, CompletableFuture<HolderLookup.Provider> registries) {
        super(p, Collections.emptySet(), Collections.singletonList(new LootTableProvider.SubProviderEntry(SubProvider::new, LootContextParamSets.BLOCK)), registries);
    }

    public static class SubProvider extends BlockLootSubProvider {

        protected SubProvider(HolderLookup.Provider provider) {
            super(Collections.emptySet(), FeatureFlagSet.of(), provider);
        }

        @Override
        protected void generate() {
            add(AFSingletons.FLUX_ACCESSOR, createSingleItemTable(AFSingletons.FLUX_ACCESSOR));
            add(AFSingletons.CHARGED_REDSTONE_BLOCK, createSingleItemTable(AFSingletons.CHARGED_REDSTONE_BLOCK));
        }

        public void generate(@NotNull BiConsumer<ResourceKey<LootTable>, LootTable.Builder> bi) {
            this.generate();
            for (var e : this.map.entrySet()) {
                bi.accept(e.getKey(), e.getValue());
            }
        }
    }
}