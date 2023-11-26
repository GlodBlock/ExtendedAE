package com.github.glodblock.extendedae.datagen;

import com.github.glodblock.extendedae.common.EAEItemAndBlock;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;

import java.util.concurrent.CompletableFuture;

public class EAEBlockTagProvider extends FabricTagProvider.BlockTagProvider {

    public EAEBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        this.getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(EAEItemAndBlock.EX_PATTERN_PROVIDER)
                .add(EAEItemAndBlock.EX_INTERFACE)
                .add(EAEItemAndBlock.WIRELESS_CONNECTOR)
                .add(EAEItemAndBlock.INGREDIENT_BUFFER)
                .add(EAEItemAndBlock.EX_DRIVE)
                .add(EAEItemAndBlock.EX_ASSEMBLER)
                .add(EAEItemAndBlock.EX_INSCRIBER)
                .add(EAEItemAndBlock.EX_CHARGER);
    }
}
