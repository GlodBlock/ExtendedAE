package com.glodblock.github.appflux.datagen;

import com.glodblock.github.appflux.common.AFRegistryHandler;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContextSource;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class AFLootTableProvider extends LootTableProvider {

    public AFLootTableProvider(PackOutput p, CompletableFuture<HolderLookup.Provider> registries) {
        super(p, Collections.emptySet(), Collections.singletonList(new LootTableProvider.SubProviderEntry(SubProvider::new, LootContextParamSets.BLOCK)), registries);
    }

    @Override
    protected void validate(@NotNull WritableRegistry<@NotNull LootTable> tables, @NotNull ValidationContextSource validationContext, ProblemReporter.@NotNull Collector problems) {
        // NO-OP
    }

    public static class SubProvider extends BlockLootSubProvider {

        protected SubProvider(HolderLookup.Provider provider) {
            super(Collections.emptySet(), FeatureFlags.REGISTRY.allFlags(), provider);
        }

        @Override
        protected void generate() {
            for (var block : AFRegistryHandler.INSTANCE.getBlocks()) {
                add(block, createSingleItemTable(block));
            }
        }

        @Override
        protected @NotNull Iterable<Block> getKnownBlocks() {
            return AFRegistryHandler.INSTANCE.getBlocks();
        }

    }
}