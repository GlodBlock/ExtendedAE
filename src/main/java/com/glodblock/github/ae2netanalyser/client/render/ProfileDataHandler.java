package com.glodblock.github.ae2netanalyser.client.render;

import com.glodblock.github.ae2netanalyser.common.items.ItemTickAnalyzer;
import com.glodblock.github.ae2netanalyser.common.me.ticker.ProfileData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.function.DoublePredicate;

@OnlyIn(Dist.CLIENT)
public class ProfileDataHandler {

    private static ProfileData DATA = null;
    private static ItemTickAnalyzer.TickConfig CONFIG = new ItemTickAnalyzer.TickConfig(60, true, true, true, true);
    private static boolean needUpdate = true;

    public static void receiveData(ProfileData data) {
        if (data != null && data.isCorrupt()) {
            DATA = null;
        } else {
            DATA = data;
        }
        needUpdate = true;
    }

    @Nullable
    public static ProfileData pullData() {
        return DATA;
    }

    public static boolean update() {
        boolean ret = needUpdate;
        needUpdate = false;
        return ret;
    }

    public static DoublePredicate renderFilter() {
        return rate -> {
            if (CONFIG.op1() && (rate < 5)) {
                return true;
            }
            if (CONFIG.op2() && (5 <= rate && rate < 100)) {
                return true;
            }
            if (CONFIG.op3() && (100 <= rate && rate < 500)) {
                return true;
            }
            return CONFIG.op4() && (rate >= 500);
        };
    }

    public static void updateConfig(ItemTickAnalyzer.TickConfig config) {
        CONFIG = config;
        needUpdate = true;
    }

}
