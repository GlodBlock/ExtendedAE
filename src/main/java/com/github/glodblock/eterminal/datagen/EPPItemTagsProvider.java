package com.github.glodblock.eterminal.datagen;

import com.github.glodblock.eterminal.EnhancedTerminal;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class EPPItemTagsProvider extends ItemTagsProvider {

    public EPPItemTagsProvider(DataGenerator p, BlockTagsProvider block, @Nullable ExistingFileHelper existingFileHelper) {
        super(p, block, EnhancedTerminal.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {

    }
}
