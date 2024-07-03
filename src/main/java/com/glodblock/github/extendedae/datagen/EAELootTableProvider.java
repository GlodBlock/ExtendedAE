package com.glodblock.github.extendedae.datagen;

import appeng.core.definitions.AEItems;
import com.glodblock.github.extendedae.api.ISpecialDrop;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.common.EAERegistryHandler;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class EAELootTableProvider extends LootTableProvider {

    public EAELootTableProvider(PackOutput p, CompletableFuture<HolderLookup.Provider> provider) {
        super(p, Collections.emptySet(), Collections.singletonList(new LootTableProvider.SubProviderEntry(SubProvider::new, LootContextParamSets.BLOCK)), provider);
    }

    public static class SubProvider extends BlockLootSubProvider {

        protected SubProvider(HolderLookup.Provider provider) {
            super(Collections.emptySet(), FeatureFlagSet.of(), provider);
        }

        @Override
        protected void generate() {
            for (var block : EAERegistryHandler.INSTANCE.getBlocks()) {
                if (!(block instanceof ISpecialDrop)) {
                    add(block, createSingleItemTable(block));
                }
            }
            add(EAESingletons.ENTRO_BUD_SMALL, createSingleItemTableWithSilkTouch(EAESingletons.ENTRO_BUD_SMALL, AEItems.FLUIX_DUST));
            add(EAESingletons.ENTRO_BUD_MEDIUM, createSingleItemTableWithSilkTouch(EAESingletons.ENTRO_BUD_MEDIUM, AEItems.FLUIX_DUST));
            add(EAESingletons.ENTRO_BUD_LARGE, createSingleItemTableWithSilkTouch(EAESingletons.ENTRO_BUD_LARGE, AEItems.FLUIX_DUST));
            add(EAESingletons.ENTRO_CLUSTER, createSilkTouchDispatchTable(EAESingletons.ENTRO_CLUSTER,
                    LootItem.lootTableItem(EAESingletons.ENTRO_CRYSTAL)
                            .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                            .apply(ApplyBonusCount.addUniformBonusCount(getEnchantment(Enchantments.FORTUNE)))
                            .apply(ApplyExplosionDecay.explosionDecay()))
            );
            add(EAESingletons.FULLY_ENTROIZED_FLUIX_BUDDING, createSingleItemTable(EAESingletons.ENTRO_DUST));
            add(EAESingletons.MOSTLY_ENTROIZED_FLUIX_BUDDING, createSingleItemTable(EAESingletons.ENTRO_DUST));
            add(EAESingletons.HALF_ENTROIZED_FLUIX_BUDDING, createSingleItemTable(EAESingletons.ENTRO_DUST));
            add(EAESingletons.HARDLY_ENTROIZED_FLUIX_BUDDING, createSingleItemTable(EAESingletons.ENTRO_DUST));
        }

        @Override
        public void generate(@NotNull BiConsumer<ResourceKey<LootTable>, LootTable.Builder> bi) {
            this.generate();
            for (var e : this.map.entrySet()) {
                bi.accept(e.getKey(), e.getValue());
            }
        }

        protected final Holder<Enchantment> getEnchantment(ResourceKey<Enchantment> key) {
            return registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(key);
        }

    }
}
