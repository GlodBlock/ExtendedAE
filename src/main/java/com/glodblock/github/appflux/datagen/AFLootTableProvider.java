package com.glodblock.github.appflux.datagen;

import com.glodblock.github.appflux.common.AFRegistryHandler;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class AFLootTableProvider extends LootTableProvider {

    public AFLootTableProvider() {
        super(
            Set.of(), 
            List.of(new LootTableProvider.SubProviderEntry(SubProvider::new, LootContextParamSets.BLOCK))
        );
    }

    public static class SubProvider extends BlockLootSubProvider {

        protected SubProvider(LootTableSubProvider.Context output) {
            super(Collections.emptySet(), FeatureFlags.REGISTRY.allFlags(), output);
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