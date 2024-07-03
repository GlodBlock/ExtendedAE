package com.glodblock.github.extendedae.config;

import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.glodium.util.GlodUtil;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntImmutableList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;
import java.util.stream.Collectors;

@EventBusSubscriber(modid = ExtendedAE.MODID, bus = EventBusSubscriber.Bus.MOD)
public class EAEConfig {

    private static final IntList defaultModifierMultiplier = new IntImmutableList(new int[]{2, 3, 5, 7});

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    private static final ModConfigSpec.IntValue EX_BUS_SPEED = BUILDER
            .comment("ME Extend Import/Export Bus speed multiplier")
            .defineInRange("exBusMultiplier", 8, 2, 128);

    private static final ModConfigSpec.DoubleValue INFINITY_CELL_ENERGY = BUILDER
            .comment("ME Infinity Cell idle energy cost (unit: AE/t)")
            .defineInRange("cost", 8.0, 0.1, 64.0);

    private static final ModConfigSpec.DoubleValue WIRELESS_CONNECTOR_RANGE = BUILDER
            .comment("The max range between two wireless connector")
            .defineInRange("range", 1000.0, 10.0, 10000.0);

    private static final ModConfigSpec.ConfigValue<List<? extends Integer>> PATTERN_MODIFIER_NUMBER = BUILDER
            .comment("Pattern modifier multipliers")
            .defineList("modifierMultipliers", defaultModifierMultiplier, EAEConfig::checkPositive);

    private static final ModConfigSpec.ConfigValue<List<? extends String>> PACKABLE_AE_DEVICE = BUILDER
            .comment("The AE device/part that can be packed by ME Packing Tape")
            .defineList("whitelist", Lists.newArrayList(
                    "extendedae:ex_interface_part",
                    "extendedae:ex_pattern_provider_part",
                    "extendedae:ex_interface",
                    "extendedae:ex_pattern_provider",
                    "extendedae:ex_drive",
                    "ae2:cable_interface",
                    "ae2:cable_pattern_provider",
                    "ae2:interface",
                    "ae2:pattern_provider",
                    "ae2:drive"
            ), o -> true);

    private static final ModConfigSpec.BooleanValue INSCRIBER_RENDER = BUILDER
            .comment("Disable Extended Inscriber's item render, it only works in client side.")
            .define("disableItemRender", false);

    private static final ModConfigSpec.IntValue OVERSIZE_MULTIPLIER = BUILDER
            .comment("Size multiplier of oversize interface")
            .defineInRange("oversizeMultiplier", 16, 2, 4096);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private static boolean checkRL(Object o) {
        return o instanceof String s && (GlodUtil.checkInvalidRL(s, BuiltInRegistries.ITEM) || GlodUtil.checkInvalidRL(s, BuiltInRegistries.FLUID));
    }

    private static boolean checkPositive(Object o) {
        return o instanceof Integer && (int) o > 0;
    }

    public static int busSpeed;
    public static double infCellCost;
    public static double wirelessMaxRange;
    public static List<ResourceLocation> tapeWhitelist;
    public static boolean disableInscriberRender;
    public static int oversizeMultiplier;
    private static List<? extends Integer> modifierMultiplier;

    public static int getPatternModifierNumber(int index) {
        if (index >= modifierMultiplier.size()) {
            return defaultModifierMultiplier.getInt(index);
        } else {
            return modifierMultiplier.get(index);
        }
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        busSpeed = EX_BUS_SPEED.get();
        infCellCost = INFINITY_CELL_ENERGY.get();
        wirelessMaxRange = WIRELESS_CONNECTOR_RANGE.get();
        tapeWhitelist = PACKABLE_AE_DEVICE.get().stream().map(ResourceLocation::parse).collect(Collectors.toList());
        disableInscriberRender = INSCRIBER_RENDER.get();
        oversizeMultiplier = OVERSIZE_MULTIPLIER.get();
        modifierMultiplier = PATTERN_MODIFIER_NUMBER.get();
    }

}
