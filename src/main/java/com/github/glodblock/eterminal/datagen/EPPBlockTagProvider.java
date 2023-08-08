package com.github.glodblock.eterminal.datagen;

import com.github.glodblock.eterminal.EnhancedTerminal;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class EPPBlockTagProvider extends BlockTagsProvider {

    public EPPBlockTagProvider(DataGenerator output, ExistingFileHelper existingFileHelper) {
        super(output, EnhancedTerminal.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {

    }
}
