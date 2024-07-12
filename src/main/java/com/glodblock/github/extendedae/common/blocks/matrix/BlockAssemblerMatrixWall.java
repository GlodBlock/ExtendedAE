package com.glodblock.github.extendedae.common.blocks.matrix;

import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixWall;
import net.minecraft.world.item.Item;

public class BlockAssemblerMatrixWall extends BlockAssemblerMatrixBase<TileAssemblerMatrixWall> {

    @Override
    public Item getPresentItem() {
        return EAESingletons.ASSEMBLER_MATRIX_WALL.asItem();
    }

}