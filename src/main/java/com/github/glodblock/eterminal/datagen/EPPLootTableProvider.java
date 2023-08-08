package com.github.glodblock.eterminal.datagen;

import appeng.datagen.providers.IAE2DataProvider;
import com.github.glodblock.eterminal.EnhancedTerminal;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Function;

public class EPPLootTableProvider extends BlockLoot implements IAE2DataProvider {
    private final Map<Block, Function<Block, LootTable.Builder>> overrides;
    private final Path outputFolder;

    public EPPLootTableProvider(Path outputFolder) {
        this.overrides = ImmutableMap.of();
        this.outputFolder = outputFolder;
    }

    public void run(@NotNull CachedOutput cache) throws IOException {
        for (var entry : Registry.BLOCK.entrySet()) {
            if (entry.getKey().location().getNamespace().equals(EnhancedTerminal.MODID)) {
                var builder = this.overrides.getOrDefault(entry.getValue(), this::defaultBuilder).apply(entry.getValue());
                DataProvider.saveStable(cache, this.toJson(builder), this.getPath(this.outputFolder, entry.getKey().location()));
            }
        }
    }

    private LootTable.Builder defaultBuilder(Block block) {
        LootPoolEntryContainer.Builder<?> entry = LootItem.lootTableItem(block);
        LootPool.Builder pool = LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(entry).when(ExplosionCondition.survivesExplosion());
        return LootTable.lootTable().withPool(pool);
    }

    private Path getPath(Path root, ResourceLocation id) {
        return root.resolve("data/" + id.getNamespace() + "/loot_tables/blocks/" + id.getPath() + ".json");
    }

    public JsonElement toJson(LootTable.Builder builder) {
        return LootTables.serialize(this.finishBuilding(builder));
    }

    public LootTable finishBuilding(LootTable.Builder builder) {
        return builder.setParamSet(LootContextParamSets.BLOCK).build();
    }

    @Override
    public @NotNull String getName() {
        return "ExtendedAE Block Drops";
    }
}
