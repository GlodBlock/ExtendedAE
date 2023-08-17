package com.glodblock.github.epp.config;

import com.glodblock.github.epp.EPP;
import com.glodblock.github.epp.util.FCUtil;
import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.format.ConfigFormats;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Config(id = EPP.MODID)
public class EPPConfig {

    public static EPPConfig INSTANCE;

    public static void onInit() {
        assert INSTANCE == null;
        INSTANCE = Configuration.registerConfig(EPPConfig.class, ConfigFormats.yaml()).getConfigInstance();
    }

    @Configurable
    @Configurable.Comment("ME Extend Import/Export Bus speed multiplier")
    @Configurable.Range(min = 2, max = 128)
    public int busSpeed = 8;

    @Configurable
    @Configurable.Comment("ME Infinity Cell idle energy cost (unit: AE/t)")
    @Configurable.DecimalRange(min = 0.1, max = 64.0)
    public double infCellCost = 8.0;

    @Configurable
    @Configurable.Comment("ME Infinity Cell types (item or fluid's id)")
    public String[] infCellTypeID = new String[] {
            "minecraft:water",
            "minecraft:cobblestone"
    };

    public List<Item> getInfCellItem() {
        return Arrays.stream(infCellTypeID).parallel().filter(
                s -> FCUtil.checkInvalidRL(s, Registry.ITEM)
        ).map(
                s -> Registry.ITEM.get(new Identifier(s))
        ).collect(Collectors.toList());
    }

    public List<Fluid> getInfCellFluid() {
        return Arrays.stream(infCellTypeID).parallel().filter(
                s -> FCUtil.checkInvalidRL(s, Registry.FLUID)
        ).map(
                s -> Registry.FLUID.get(new Identifier(s))
        ).collect(Collectors.toList());
    }

    @Configurable
    @Configurable.Comment("The AE device/part that can be packed by ME Packing Tape")
    public String[] tapeWhitelist = new String[] {
            "expatternprovider:ex_interface_part",
            "expatternprovider:ex_pattern_provider_part",
            "expatternprovider:ex_interface",
            "expatternprovider:ex_pattern_provider",
            "ae2:cable_interface",
            "ae2:cable_pattern_provider",
            "ae2:interface",
            "ae2:pattern_provider",
            "ae2:drive"
    };

}
