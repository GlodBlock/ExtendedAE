package com.github.glodblock.epp.datagen;

import com.github.glodblock.epp.EPP;
import com.github.glodblock.epp.common.EPPItemAndBlock;
import com.github.glodblock.epp.util.EPPTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class EPPItemTagsProvider extends ItemTagsProvider {

    public EPPItemTagsProvider(DataGenerator p, BlockTagsProvider block, @Nullable ExistingFileHelper existingFileHelper) {
        super(p, block, EPP.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(EPPTags.EX_PATTERN_PROVIDER)
                .add(EPPItemAndBlock.EX_PATTERN_PROVIDER_PART)
                .add(EPPItemAndBlock.EX_PATTERN_PROVIDER.asItem());
        tag(EPPTags.EX_INTERFACE)
                .add(EPPItemAndBlock.EX_INTERFACE_PART)
                .add(EPPItemAndBlock.EX_INTERFACE.asItem());
    }
}
