package com.glodblock.github.extendedae.config;

import appeng.api.stacks.AEKeyType;
import com.glodblock.github.extendedae.ExtendedAE;
import it.unimi.dsi.fastutil.ints.IntImmutableList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@EventBusSubscriber(modid = ExtendedAE.MODID)
public class EAEConfig {

    private static final IntList defaultModifierMultiplier = new IntImmutableList(new int[]{2, 3, 5, 7});

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    private static final ModConfigSpec.IntValue EX_BUS_SPEED = BUILDER
            .comment("ME Extend Import/Export Bus speed multiplier")
            .defineInRange("device.extended_io_bus_multiplier", 8, 2, 128);

    private static final ModConfigSpec.DoubleValue INFINITY_CELL_ENERGY = BUILDER
            .comment("ME Infinity Cell idle energy cost (unit: AE/t)")
            .defineInRange("item.infinity_cell_energy_cost", 8.0, 0.1, 64.0);

    private static final ModConfigSpec.DoubleValue WIRELESS_CONNECTOR_RANGE = BUILDER
            .comment("The max range between two wireless connector")
            .defineInRange("device.wireless_connector_max_range", 1000.0, 10.0, 10000.0);

    private static final ModConfigSpec.DoubleValue WIRELESS_CONNECTOR_POWER_MULTIPLIER = BUILDER
            .comment("Power usage multiplier for wireless connectors")
            .defineInRange("device.wireless_connector_power_multiplier", 1.0, 0.0, 100.0);

    @SuppressWarnings("deprecation")
    private static final ModConfigSpec.ConfigValue<@NotNull List<? extends Integer>> PATTERN_MODIFIER_NUMBER = BUILDER
            .comment("Pattern modifier multipliers")
            .defineList("item.pattern_modifier_multipliers", defaultModifierMultiplier, EAEConfig::checkPositive);

    @SuppressWarnings("deprecation")
    private static final ModConfigSpec.ConfigValue<@NotNull List<? extends String>> PACKABLE_AE_DEVICE = BUILDER
            .comment("The AE device/part that can be packed by ME Packing Tape")
            .defineList("item.me_packing_tape_whitelist", List.of(
                    "extendedae:ex_interface_part",
                    "extendedae:ex_pattern_provider_part",
                    "extendedae:ex_interface",
                    "extendedae:ex_pattern_provider",
                    "extendedae:ex_drive",
                    "extendedae:oversize_interface",
                    "extendedae:oversize_interface_part",
                    "ae2:cable_interface",
                    "ae2:cable_pattern_provider",
                    "ae2:interface",
                    "ae2:pattern_provider",
                    "ae2:drive"
            ), _ -> true);

    private static final ModConfigSpec.BooleanValue INSCRIBER_RENDER = BUILDER
            .comment("Disable Extended Inscriber's item render, it only works in client side")
            .define("client.disable_inscriber_item_render", false);

    private static final ModConfigSpec.IntValue OVERSIZE_MULTIPLIER = BUILDER
            .comment("Size multiplier of oversize interface")
            .defineInRange("device.oversize_interface_multiplier", 16, 2, 4096);

    @SuppressWarnings("deprecation")
    private static final ModConfigSpec.ConfigValue<@NotNull List<? extends String>> CUSTOM_OVERSIZE_MULTIPLIER = BUILDER
            .comment("Set multiplier for specific AEKeyType in oversize interface")
            .defineList("device.custom_oversize_interface_multiplier", List.of(
                    "appbot:mana 2",
                    "appflux:flux 4"
            ), _ -> true);

    private static final ModConfigSpec.BooleanValue CRYSTAL_INSCRIBER = BUILDER
            .comment("Allow Crystal Assembler to do processor inscriber recipes")
            .define("device.enable_crystal_assembler_inscribe_processors", true);

    private static final ModConfigSpec.IntValue ASSEMBLER_MATRIX_SIZE = BUILDER
            .comment("The max size of Assembler Matrix")
            .defineInRange("device.assembler_matrix_max_size", 6, 3, 16);

    private static final ModConfigSpec.BooleanValue DEBUG_MODE = BUILDER
            .comment("Enable debug logging.")
            .define("misc.debug_mode", false);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private static boolean checkPositive(Object o) {
        return o instanceof Integer && (int) o > 0;
    }

    public static int busSpeed;
    public static double infCellCost;
    public static double wirelessMaxRange;
    public static double wirelessPowerMultiplier;
    public static List<Identifier> tapeWhitelist;
    public static boolean disableInscriberRender;
    private static int oversizeMultiplier;
    private static Map<Identifier, Integer> customOversizeMultiplier;
    private static List<? extends Integer> modifierMultiplier;
    public static boolean allowAssemblerCircuits;
    public static int assemblerMatrixSize;
    public static boolean debugMode;

    public static int getPatternModifierNumber(int index) {
        if (index >= modifierMultiplier.size()) {
            return defaultModifierMultiplier.getInt(index);
        } else {
            return modifierMultiplier.get(index);
        }
    }

    public static int getOversizeMultiplier(AEKeyType type) {
        return customOversizeMultiplier.getOrDefault(type.getId(), oversizeMultiplier);
    }

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent event) {
        if (event.getConfig().getSpec() == SPEC) {
            busSpeed = EX_BUS_SPEED.get();
            infCellCost = INFINITY_CELL_ENERGY.get();
            wirelessMaxRange = WIRELESS_CONNECTOR_RANGE.get();
            wirelessPowerMultiplier = WIRELESS_CONNECTOR_POWER_MULTIPLIER.get();
            tapeWhitelist = PACKABLE_AE_DEVICE.get().stream().map(Identifier::parse).collect(Collectors.toList());
            disableInscriberRender = INSCRIBER_RENDER.get();
            oversizeMultiplier = OVERSIZE_MULTIPLIER.get();
            customOversizeMultiplier = new Object2IntOpenHashMap<>();
            CUSTOM_OVERSIZE_MULTIPLIER.get().stream()
                    .map(EAEConfig::parseOversizeMultiplier)
                    .filter(Objects::nonNull)
                    .forEach(p -> customOversizeMultiplier.put(p.getKey(), p.getValue()));
            modifierMultiplier = PATTERN_MODIFIER_NUMBER.get();
            allowAssemblerCircuits = CRYSTAL_INSCRIBER.get();
            assemblerMatrixSize = ASSEMBLER_MATRIX_SIZE.get();
            debugMode = DEBUG_MODE.get();
        }
    }

    private static Pair<Identifier, Integer> parseOversizeMultiplier(String s) {
        try {
            var pair = s.split(" ");
            return Pair.of(Identifier.parse(pair[0]), Integer.decode(pair[1]));
        } catch (Throwable t) {
            return null;
        }
    }

}
