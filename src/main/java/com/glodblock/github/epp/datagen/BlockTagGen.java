package com.glodblock.github.epp.datagen;

import com.glodblock.github.epp.common.EPPItemAndBlock;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagKey;

public class BlockTagGen extends FabricTagProvider.BlockTagProvider {

    public BlockTagGen(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    protected void generateTags() {
        TagKey<Block> pickaxe = BlockTags.PICKAXE_MINEABLE;
        this.getOrCreateTagBuilder(pickaxe)
                .add(EPPItemAndBlock.EX_PATTERN_PROVIDER)
                .add(EPPItemAndBlock.EX_INTERFACE);
    }
}
