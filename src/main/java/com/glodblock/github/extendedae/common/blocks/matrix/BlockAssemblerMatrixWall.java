package com.glodblock.github.extendedae.common.blocks.matrix;

import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixWall;
import net.minecraft.world.item.Item;

public class BlockAssemblerMatrixWall extends BlockAssemblerMatrixBase<TileAssemblerMatrixWall> {

    public BlockAssemblerMatrixWall(Properties props) {
        super(props);
    }

    @Override
    public Item getPresentItem() {
        return EAESingletons.ASSEMBLER_MATRIX_WALL.asItem();
    }

}