package com.glodblock.github.extendedae.util;

import appeng.api.parts.IPart;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.networking.CableBusBlockEntity;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenCustomHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.IForgeRegistry;

public class FCUtil {

    public static void replaceTile(Level world, BlockPos pos, BlockEntity oldTile, BlockEntity newTile, BlockState newBlock) {
        var contents = oldTile.serializeNBT();
        world.removeBlockEntity(pos);
        world.removeBlock(pos, false);
        world.setBlock(pos, newBlock, 3);
        world.setBlockEntity(newTile);
        newTile.deserializeNBT(contents);
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

    public static boolean checkInvalidRL(String rl, IForgeRegistry<?> registry) {
        return checkInvalidRL(new ResourceLocation(rl), registry);
    }

    public static boolean checkInvalidRL(ResourceLocation rl, IForgeRegistry<?> registry) {
        return registry.containsKey(rl);
    }

    public static double clamp(double num, double floor, double ceil) {
        return Math.min(ceil, Math.max(floor, num));
    }

}
