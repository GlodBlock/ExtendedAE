package com.glodblock.github.extendedae.datagen;

import com.glodblock.github.extendedae.api.ISpecialDrop;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.common.EAERegistryHandler;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContextSource;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class EAELootTableProvider extends LootTableProvider {

    public EAELootTableProvider(PackOutput p, CompletableFuture<HolderLookup.Provider> provider) {
        super(p, Collections.emptySet(), Collections.singletonList(new LootTableProvider.SubProviderEntry(SubProvider::new, LootContextParamSets.BLOCK)), provider);
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
            for (var block : EAERegistryHandler.INSTANCE.getBlocks()) {
                if (!(block instanceof ISpecialDrop)) {
                    add(block, createSingleItemTable(block));
                }
            }
            add(EAESingletons.ENTRO_BUD_SMALL.get(), createSingleItemTableWithSilkTouch(EAESingletons.ENTRO_BUD_SMALL.get(), EAESingletons.ENTRO_SHARD));
            add(EAESingletons.ENTRO_BUD_MEDIUM.get(), createSingleItemTableWithSilkTouch(EAESingletons.ENTRO_BUD_MEDIUM.get(), EAESingletons.ENTRO_SHARD));
            add(EAESingletons.ENTRO_BUD_LARGE.get(), createSingleItemTableWithSilkTouch(EAESingletons.ENTRO_BUD_LARGE.get(), EAESingletons.ENTRO_SHARD));
            add(EAESingletons.ENTRO_CLUSTER.get(), createSilkTouchDispatchTable(EAESingletons.ENTRO_CLUSTER.get(),
                    ((LootPoolSingletonContainer.Builder<?>) LootItem.lootTableItem(EAESingletons.ENTRO_CRYSTAL)
                            .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                            .apply(ApplyBonusCount.addUniformBonusCount(getEnchantment())))
                            .apply(ApplyExplosionDecay.explosionDecay())
                    )
            );
            add(EAESingletons.FULLY_ENTROIZED_FLUIX_BUDDING.get(), createSingleItemTable(EAESingletons.ENTRO_DUST));
            add(EAESingletons.MOSTLY_ENTROIZED_FLUIX_BUDDING.get(), createSingleItemTable(EAESingletons.ENTRO_DUST));
            add(EAESingletons.HALF_ENTROIZED_FLUIX_BUDDING.get(), createSingleItemTable(EAESingletons.ENTRO_DUST));
            add(EAESingletons.HARDLY_ENTROIZED_FLUIX_BUDDING.get(), createSingleItemTable(EAESingletons.ENTRO_DUST));
        }

        protected final Holder<@NotNull Enchantment> getEnchantment() {
            return registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE);
        }

        @Override
        protected @NotNull Iterable<Block> getKnownBlocks() {
            return EAERegistryHandler.INSTANCE.getBlocks();
        }

    }
}
