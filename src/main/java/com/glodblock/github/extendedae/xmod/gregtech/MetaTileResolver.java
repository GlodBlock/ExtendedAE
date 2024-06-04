package com.glodblock.github.extendedae.xmod.gregtech;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class MetaTileResolver {

    public static boolean check(Object obj) {
        return obj instanceof MetaMachine;
    }

    public static BlockPos getBlockPos(Object mte) {
        return ((MetaMachine) mte).getPos();
    }

    public static Level getLevel(Object mte) {
        return ((MetaMachine) mte).getLevel();
    }

}
