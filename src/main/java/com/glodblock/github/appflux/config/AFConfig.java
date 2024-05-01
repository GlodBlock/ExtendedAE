package com.glodblock.github.appflux.config;

import com.glodblock.github.appflux.AppFlux;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = AppFlux.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AFConfig {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.IntValue FLUX_PER_BYTE = BUILDER
            .comment("FE stored per byte.")
            .defineInRange("amount", 1024 * 1024 * 4, 1, Integer.MAX_VALUE);
    private static final ForgeConfigSpec.LongValue FLUX_ACCESSOR_IO = BUILDER
            .comment("The I/O limit of Flux Accessor. 0 means no limitation.")
            .defineInRange("io_limit", 0L, 0L, Integer.MAX_VALUE);
    private static final ForgeConfigSpec.BooleanValue NETWORK_CHARGE = BUILDER
            .comment("Allow Flux Accessor to charge ME network with stored FE.")
            .define("enable", false);
    private static final ForgeConfigSpec.BooleanValue FE_IMPORT = BUILDER
            .comment("Allow ME Import Bus to pull FE.")
            .define("enable_FE_pull", false);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static int getFluxPerByte() {
        return fluxPerByte;
    }

    public static long getFluxAccessorIO() {
        return fluxAccessorIO <= 0 ? Long.MAX_VALUE : fluxAccessorIO;
    }

    public static boolean selfCharge() {
        return selfCharge;
    }

    public static boolean pullFE() {
        return pullFE;
    }

    private static int fluxPerByte;
    private static long fluxAccessorIO;
    private static boolean selfCharge;
    private static boolean pullFE;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        fluxPerByte = FLUX_PER_BYTE.get();
        fluxAccessorIO = FLUX_ACCESSOR_IO.get();
        selfCharge = NETWORK_CHARGE.get();
        pullFE = FE_IMPORT.get();
    }

}
