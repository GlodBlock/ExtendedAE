package com.glodblock.github.epp.util;

import appeng.blockentity.AEBaseBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FCUtil {

    public static void replaceTile(World world, BlockPos pos, BlockEntity oldTile, BlockEntity newTile, BlockState newBlock) {
        var contents = oldTile.createNbt();
        world.removeBlockEntity(pos);
        world.removeBlock(pos, false);
        world.setBlockState(pos, newBlock, 3);
        world.addBlockEntity(newTile);
        newTile.readNbt(contents);
        if (newTile instanceof AEBaseBlockEntity aeTile) {
            aeTile.markForUpdate();
        } else {
            newTile.markDirty();
        }
    }

}
