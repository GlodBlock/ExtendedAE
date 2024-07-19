package com.glodblock.github.appflux.common.blocks;

import net.minecraft.world.level.block.Block;

import static appeng.block.AEBaseBlock.stoneProps;

public class BlockChargedRedstoneBlock extends Block {

    public BlockChargedRedstoneBlock() {
        super(stoneProps().lightLevel(state -> 12).strength(3, 5).requiresCorrectToolForDrops());
    }

}
