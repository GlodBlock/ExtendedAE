package com.glodblock.github.extendedae.datagen;

import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.function.BiConsumer;

public class EPPLootTableProvider extends LootTableProvider {

    public EPPLootTableProvider(PackOutput p) {
        super(p, Collections.emptySet(), Collections.singletonList(new LootTableProvider.SubProviderEntry(SubProvider::new, LootContextParamSets.BLOCK)));
    }

    public static class SubProvider extends BlockLootSubProvider {

        protected SubProvider() {
            super(Collections.emptySet(), FeatureFlagSet.of(), new HashMap<>());
        }

        @Override
        protected void generate() {
            add(EPPItemAndBlock.EX_PATTERN_PROVIDER, createSingleItemTable(EPPItemAndBlock.EX_PATTERN_PROVIDER));
            add(EPPItemAndBlock.EX_INTERFACE, createSingleItemTable(EPPItemAndBlock.EX_INTERFACE));
            add(EPPItemAndBlock.WIRELESS_CONNECTOR, createSingleItemTable(EPPItemAndBlock.WIRELESS_CONNECTOR));
            add(EPPItemAndBlock.INGREDIENT_BUFFER, createSingleItemTable(EPPItemAndBlock.INGREDIENT_BUFFER));
            add(EPPItemAndBlock.EX_DRIVE, createSingleItemTable(EPPItemAndBlock.EX_DRIVE));
            add(EPPItemAndBlock.EX_ASSEMBLER, createSingleItemTable(EPPItemAndBlock.EX_ASSEMBLER));
            add(EPPItemAndBlock.EX_INSCRIBER, createSingleItemTable(EPPItemAndBlock.EX_INSCRIBER));
            add(EPPItemAndBlock.EX_CHARGER, createSingleItemTable(EPPItemAndBlock.EX_CHARGER));
        }

        public void generate(@NotNull BiConsumer<ResourceLocation, LootTable.Builder> bi) {
            this.generate();
            for (var e : this.map.entrySet()) {
                bi.accept(e.getKey(), e.getValue());
            }
        }
    }
}
