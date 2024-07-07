package com.glodblock.github.appflux.xmod.jade;

import appeng.helpers.InterfaceLogicHost;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import com.glodblock.github.appflux.common.tileentities.TileFluxAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class JadeBlacklist {

    public static final List<Predicate<Object>> BLACK_LIST = new ArrayList<>();

    static {
        BLACK_LIST.add(target -> target instanceof InterfaceLogicHost || target instanceof PatternProviderLogicHost || target instanceof TileFluxAccessor);
    }

    public static boolean shouldRemove(Object obj) {
        for (var pd : BLACK_LIST) {
            if (pd.test(obj)) {
                return true;
            }
        }
        return false;
    }


}
