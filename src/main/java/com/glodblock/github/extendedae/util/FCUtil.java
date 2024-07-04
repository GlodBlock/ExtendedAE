package com.glodblock.github.extendedae.util;

import appeng.api.inventories.InternalInventory;
import appeng.api.parts.IPart;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.networking.CableBusBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class FCUtil {

    public static void replaceTile(Level world, BlockPos pos, BlockEntity oldTile, BlockEntity newTile, BlockState newBlock) {
        var contents = oldTile.saveWithFullMetadata(world.registryAccess());
        world.removeBlockEntity(pos);
        world.removeBlock(pos, false);
        world.setBlock(pos, newBlock, 3);
        world.setBlockEntity(newTile);
        newTile.loadWithComponents(contents, world.registryAccess());
        if (newTile instanceof AEBaseBlockEntity aeTile) {
            aeTile.markForUpdate();
        } else {
            newTile.setChanged();
        }
    }

    public static IPart getPart(BlockEntity te, Direction face) {
        if (te instanceof CableBusBlockEntity cable) {
            return cable.getPart(face);
        }
        return null;
    }

    public static boolean ejectInv(Level world, BlockPos pos, InternalInventory inv) {
        for (var dir : Direction.values()) {
            var target = InternalInventory.wrapExternal(world, pos.relative(dir), dir.getOpposite());
            if (target != null) {
                int startItems = inv.getStackInSlot(0).getCount();
                inv.insertItem(0, target.addItems(inv.extractItem(0, 64, false)), false);
                int endItems = inv.getStackInSlot(0).getCount();
                if (startItems != endItems) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int speedCardMap(int card) {
        return speedCardMap(card, 1);
    }

    public static int speedCardMap(int card, int multi) {
        return multi * switch (card) {
            default -> 2;
            case 1 -> 3;
            case 2 -> 5;
            case 3 -> 10;
            case 4 -> 50;
        };
    }

    public static String[] trimSplit(String str) {
        var sp = str.split(",");
        for (int i = 0; i < sp.length; i ++) {
            sp[i] = sp[i].trim();
        }
        return sp;
    }

}
