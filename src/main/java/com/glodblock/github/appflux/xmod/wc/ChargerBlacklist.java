package com.glodblock.github.appflux.xmod.wc;

import appeng.blockentity.networking.CableBusBlockEntity;
import appeng.helpers.InterfaceLogicHost;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ChargerBlacklist {

    public final static List<Predicate<BlockEntity>> BLACKLIST = new ArrayList<>();

    static {
        BLACKLIST.add(te -> te instanceof PatternProviderLogicHost);
        BLACKLIST.add(te -> te instanceof InterfaceLogicHost);
        BLACKLIST.add(te -> te instanceof CableBusBlockEntity);
    }

}