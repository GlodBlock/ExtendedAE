package com.glodblock.github.appflux.config;

import com.glodblock.github.appflux.AppFlux;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = AppFlux.MODID, bus = EventBusSubscriber.Bus.MOD)
public class AFConfig {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.IntValue FLUX_PER_BYTE = BUILDER
            .comment("FE can be stored per byte.")
            .defineInRange("flux_cell.amount", 1024 * 32, 1, Integer.MAX_VALUE);
    private static final ModConfigSpec.LongValue FLUX_ACCESSOR_IO = BUILDER
            .comment("The I/O limit of Flux Accessor. 0 means no limitation.")
            .defineInRange("flux_accessor.io_limit", 0L, 0L, Integer.MAX_VALUE);
    private static final ModConfigSpec.BooleanValue NETWORK_CHARGE = BUILDER
            .comment("Allow Flux Accessor to charge ME network with stored FE.")
            .define("flux_accessor.enable", false);
    private static final ModConfigSpec.BooleanValue ENABLE_IMPORT = BUILDER
            .comment("Allow ME Import Bus to import energy like items/fluids.")
            .define("misc.enable", false);

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static int getFluxPerByte() {
        return fluxPerByte;
    }

    public static long getFluxAccessorIO() {
        return fluxAccessorIO <= 0 ? Long.MAX_VALUE : fluxAccessorIO;
    }

    public static boolean selfCharge() {
        return selfCharge;
    }

    public static boolean allowImport() {
        return allowImportBus;
    }

    private static int fluxPerByte;
    private static long fluxAccessorIO;
    private static boolean selfCharge;
    private static boolean allowImportBus;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        fluxPerByte = FLUX_PER_BYTE.get();
        fluxAccessorIO = FLUX_ACCESSOR_IO.get();
        selfCharge = NETWORK_CHARGE.get();
        allowImportBus = ENABLE_IMPORT.get();
    }

}
