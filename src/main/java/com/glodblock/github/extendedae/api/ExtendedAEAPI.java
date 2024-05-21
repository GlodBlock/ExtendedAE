package com.glodblock.github.extendedae.api;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import com.glodblock.github.extendedae.common.EAEItemAndBlock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExtendedAEAPI {

    private static final List<ICrystalFixer> crystalFixers = new ArrayList<>();

    public static void addCrystal(ICrystalFixer crystalFixer) {
        crystalFixers.add(crystalFixer);
    }

    public static List<ICrystalFixer> getCrystalFixers() {
        return crystalFixers;
    }

    static {
        addCrystal(new CrystalFixer(new ArrayList<>(Arrays.asList(
                AEBlocks.QUARTZ_BLOCK.block(),
                AEBlocks.DAMAGED_BUDDING_QUARTZ.block(),
                AEBlocks.CHIPPED_BUDDING_QUARTZ.block(),
                AEBlocks.FLAWED_BUDDING_QUARTZ.block()
        )), AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED.asItem()));
        addCrystal(new CrystalFixer(new ArrayList<>(Arrays.asList(
                EAEItemAndBlock.ENTRO_BLOCK,
                EAEItemAndBlock.HARDLY_ENTROIZED_FLUIX_BUDDING,
                EAEItemAndBlock.HALF_ENTROIZED_FLUIX_BUDDING,
                EAEItemAndBlock.MOSTLY_ENTROIZED_FLUIX_BUDDING
        )), EAEItemAndBlock.ENTRO_CRYSTAL));
    }
}
