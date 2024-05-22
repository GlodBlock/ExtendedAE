package com.glodblock.github.extendedae.api;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface ICrystalFixer {


    boolean canFix(BlockState crystal, ItemStack fuel);

    Block getNextCrystalBlock(BlockState crystalBlock);

    boolean isCrystal(Block block);

    void setFuelType(Item fuel);

    Item getFuelType();
}
