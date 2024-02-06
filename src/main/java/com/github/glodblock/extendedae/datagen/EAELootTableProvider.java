package com.github.glodblock.extendedae.datagen;

import com.github.glodblock.extendedae.common.EAEItemAndBlock;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;

public class EAELootTableProvider extends FabricBlockLootTableProvider {

    public EAELootTableProvider(FabricDataOutput p) {
        super(p);
    }

    @Override
    public void generate() {
        dropSelf(EAEItemAndBlock.EX_PATTERN_PROVIDER);
        dropSelf(EAEItemAndBlock.EX_INTERFACE);
        dropSelf(EAEItemAndBlock.WIRELESS_CONNECTOR);
        dropSelf(EAEItemAndBlock.INGREDIENT_BUFFER);
        dropSelf(EAEItemAndBlock.EX_DRIVE);
        dropSelf(EAEItemAndBlock.EX_ASSEMBLER);
        dropSelf(EAEItemAndBlock.EX_INSCRIBER);
        dropSelf(EAEItemAndBlock.EX_CHARGER);
        dropSelf(EAEItemAndBlock.FISHBIG);
    }

}
