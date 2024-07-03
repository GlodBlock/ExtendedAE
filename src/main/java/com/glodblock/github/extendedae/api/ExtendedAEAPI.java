package com.glodblock.github.extendedae.api;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import com.glodblock.github.extendedae.common.EAESingletons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExtendedAEAPI {
    public static final ExtendedAEAPI INSTANCE = new ExtendedAEAPI();
    private final List<ICrystalFixer> crystalFixers = new ArrayList<>();

    public ExtendedAEAPI() {
        addCrystal(new CrystalFixer(Arrays.asList(AEBlocks.QUARTZ_BLOCK.block(), AEBlocks.DAMAGED_BUDDING_QUARTZ.block(), AEBlocks.CHIPPED_BUDDING_QUARTZ.block(), AEBlocks.FLAWED_BUDDING_QUARTZ.block()), AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED.asItem()));
        addCrystal(new CrystalFixer(Arrays.asList(EAESingletons.HARDLY_ENTROIZED_FLUIX_BUDDING, EAESingletons.HALF_ENTROIZED_FLUIX_BUDDING, EAESingletons.MOSTLY_ENTROIZED_FLUIX_BUDDING), EAESingletons.ENTRO_CRYSTAL));
    }

    public void addCrystal(ICrystalFixer crystalFixer) {
        crystalFixers.add(crystalFixer);
    }

    public List<ICrystalFixer> getCrystalFixers() {
        return crystalFixers;
    }

}
