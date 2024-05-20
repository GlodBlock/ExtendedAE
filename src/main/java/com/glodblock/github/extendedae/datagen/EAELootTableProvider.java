package com.glodblock.github.extendedae.datagen;

import com.glodblock.github.extendedae.api.ISpecialDrop;
import com.glodblock.github.extendedae.common.EAEItemAndBlock;
import com.glodblock.github.extendedae.common.EAERegistryHandler;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Items;
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
import java.util.HashMap;
import java.util.function.BiConsumer;

public class EAELootTableProvider extends LootTableProvider {

    public EAELootTableProvider(PackOutput p) {
        super(p, Collections.emptySet(), Collections.singletonList(new LootTableProvider.SubProviderEntry(SubProvider::new, LootContextParamSets.BLOCK)));
    }

    public static class SubProvider extends BlockLootSubProvider {

        protected SubProvider() {
            super(Collections.emptySet(), FeatureFlagSet.of(), new HashMap<>());
        }

        @Override
        protected void generate() {
            for (var block : EAERegistryHandler.INSTANCE.getBlocks()) {
                if (!(block instanceof ISpecialDrop)) {
                    add(block, createSingleItemTable(block));
                }
            }
            add(EAEItemAndBlock.ENTRO_BUD_SMALL, createSingleItemTableWithSilkTouch(EAEItemAndBlock.ENTRO_BUD_SMALL, Items.AIR));
            add(EAEItemAndBlock.ENTRO_BUD_MEDIUM, createSingleItemTableWithSilkTouch(EAEItemAndBlock.ENTRO_BUD_MEDIUM, Items.AIR));
            add(EAEItemAndBlock.ENTRO_BUD_LARGE, createSingleItemTableWithSilkTouch(EAEItemAndBlock.ENTRO_BUD_LARGE, Items.AIR));
            add(EAEItemAndBlock.ENTRO_CLUSTER, createSilkTouchDispatchTable(EAEItemAndBlock.ENTRO_CLUSTER,
                    LootItem.lootTableItem(EAEItemAndBlock.ENTRO_CRYSTAL)
                            .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                            .apply(ApplyBonusCount.addUniformBonusCount(Enchantments.BLOCK_FORTUNE))
                            .apply(ApplyExplosionDecay.explosionDecay()))
            );
            add(EAEItemAndBlock.FULLY_ENTROIZED_FLUIX_BUDDING, createSingleItemTable(EAEItemAndBlock.ENTRO_DUST));
            add(EAEItemAndBlock.MOSTLY_ENTROIZED_FLUIX_BUDDING, createSingleItemTable(EAEItemAndBlock.ENTRO_DUST));
            add(EAEItemAndBlock.HALF_ENTROIZED_FLUIX_BUDDING, createSingleItemTable(EAEItemAndBlock.ENTRO_DUST));
            add(EAEItemAndBlock.HARDLY_ENTROIZED_FLUIX_BUDDING, createSingleItemTable(EAEItemAndBlock.ENTRO_DUST));
        }

        public void generate(@NotNull BiConsumer<ResourceLocation, LootTable.Builder> bi) {
            this.generate();
            for (var e : this.map.entrySet()) {
                bi.accept(e.getKey(), e.getValue());
            }
        }

    }
}
