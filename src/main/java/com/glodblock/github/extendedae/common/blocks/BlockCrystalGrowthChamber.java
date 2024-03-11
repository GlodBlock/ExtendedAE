package com.glodblock.github.extendedae.common.blocks;

import appeng.block.AEBaseEntityBlock;
import com.glodblock.github.extendedae.common.tileentities.TileCrystalGrowthChamber;

public class BlockCrystalGrowthChamber extends AEBaseEntityBlock<TileCrystalGrowthChamber> {
    public BlockCrystalGrowthChamber() {
        super(stoneProps().noOcclusion());
    }
}
