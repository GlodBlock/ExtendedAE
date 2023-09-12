package com.github.glodblock.epp.datagen;

import com.github.glodblock.epp.EPP;
import com.github.glodblock.epp.common.EPPItemAndBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;

public class EPPBlockTagProvider extends BlockTagsProvider {

    public EPPBlockTagProvider(DataGenerator output, ExistingFileHelper existingFileHelper) {
        super(output, EPP.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        TagKey<Block> pickaxe = BlockTags.MINEABLE_WITH_PICKAXE;
        tag(pickaxe)
                .add(EPPItemAndBlock.EX_PATTERN_PROVIDER)
                .add(EPPItemAndBlock.EX_INTERFACE)
                .add(EPPItemAndBlock.INGREDIENT_BUFFER)
                .add(EPPItemAndBlock.EX_DRIVE);
    }
}
