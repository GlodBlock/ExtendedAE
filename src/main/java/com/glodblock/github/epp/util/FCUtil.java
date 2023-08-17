package com.glodblock.github.epp.util;

import appeng.blockentity.AEBaseBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
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

    public static boolean checkInvalidRL(String rl, Registry<?> registry) {
        return checkInvalidRL(new Identifier(rl), registry);
    }

    public static boolean checkInvalidRL(Identifier rl, Registry<?> registry) {
        return registry.containsId(rl);
    }

}
