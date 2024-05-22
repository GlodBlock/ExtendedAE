package com.glodblock.github.extendedae.api;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CrystalFixer implements ICrystalFixer {

    private final ArrayList<Block> crystalBlocks = new ArrayList<>();
    private Item fuel;

    public CrystalFixer(List<Block> crystalBlocks, Item fuel) {
        this.crystalBlocks.addAll(crystalBlocks);
        this.setFuelType(fuel);
    }

    @Override
    public void setFuelType(Item fuel) {
        this.fuel = fuel;
    }

    @Override
    public boolean canFix(BlockState crystal, ItemStack stack) {
        if (crystalBlocks.size() <= 1 || stack.getItem() != fuel) {
            return false;
        }
        for (int i = 0; i < crystalBlocks.size() - 1; i++) {
            if (crystal.is(crystalBlocks.get(i))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Block getNextCrystalBlock(BlockState crystalBlock) {
        Iterator<Block> iterator = crystalBlocks.iterator();

        while (iterator.hasNext()) {
            if (crystalBlock.is(iterator.next()) && iterator.hasNext()) {
                return iterator.next();
            }
        }
        return null;
    }

    @Override
    public boolean isCrystal(Block block) {
        return crystalBlocks.contains(block);
    }

    @Override
    public Item getFuelType() {
        return this.fuel;
    }


}
