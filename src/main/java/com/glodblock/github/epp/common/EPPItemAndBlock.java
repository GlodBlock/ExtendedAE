package com.glodblock.github.epp.common;

import appeng.items.parts.PartItem;
import com.glodblock.github.epp.EPP;
import com.glodblock.github.epp.common.blocks.BlockExInterface;
import com.glodblock.github.epp.common.blocks.BlockExPatternProvider;
import com.glodblock.github.epp.common.items.*;
import com.glodblock.github.epp.common.parts.PartExExportBus;
import com.glodblock.github.epp.common.parts.PartExImportBus;
import com.glodblock.github.epp.common.parts.PartExInterface;
import com.glodblock.github.epp.common.parts.PartExPatternProvider;
import com.glodblock.github.epp.common.tiles.TileExInterface;
import com.glodblock.github.epp.common.tiles.TileExPatternProvider;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class EPPItemAndBlock {

    public static BlockExPatternProvider EX_PATTERN_PROVIDER;
    public static PartItem<PartExPatternProvider> EX_PATTERN_PROVIDER_PART;
    public static ItemPatternProviderUpgrade PATTERN_PROVIDER_UPGRADE;
    public static ItemInterfaceUpgrade INTERFACE_UPGRADE;
    public static ItemIOBusUpgrade IO_BUS_UPGRADE;
    public static BlockExInterface EX_INTERFACE;
    public static PartItem<PartExInterface> EX_INTERFACE_PART;
    public static InfinityCell INFINITY_CELL;
    public static PartItem<PartExExportBus> EX_EXPORT_BUS;
    public static PartItem<PartExImportBus> EX_IMPORT_BUS;
    public static final ItemGroup TAB = FabricItemGroupBuilder.build(EPP.id("eppitems"), () -> new ItemStack(EX_PATTERN_PROVIDER));

    public static void init(RegistryHandler regHandler) {
        EX_PATTERN_PROVIDER = new BlockExPatternProvider();
        EX_PATTERN_PROVIDER_PART = new PartItem<>(new Item.Settings().group(EPPItemAndBlock.TAB), PartExPatternProvider.class, PartExPatternProvider::new);
        EX_INTERFACE = new BlockExInterface();
        EX_INTERFACE_PART = new PartItem<>(new Item.Settings().group(EPPItemAndBlock.TAB), PartExInterface.class, PartExInterface::new);
        INFINITY_CELL = new InfinityCell();
        EX_EXPORT_BUS = new PartItem<>(new Item.Settings().group(EPPItemAndBlock.TAB), PartExExportBus.class, PartExExportBus::new);
        EX_IMPORT_BUS = new PartItem<>(new Item.Settings().group(EPPItemAndBlock.TAB), PartExImportBus.class, PartExImportBus::new);
        IO_BUS_UPGRADE = new ItemIOBusUpgrade();
        INTERFACE_UPGRADE = new ItemInterfaceUpgrade();
        PATTERN_PROVIDER_UPGRADE = new ItemPatternProviderUpgrade();
        regHandler.block("ex_pattern_provider", EX_PATTERN_PROVIDER, TileExPatternProvider.class, TileExPatternProvider.TYPE);
        regHandler.block("ex_interface", EX_INTERFACE, TileExInterface.class, TileExInterface.TYPE);
        regHandler.item("ex_pattern_provider_part", EX_PATTERN_PROVIDER_PART);
        regHandler.item("ex_interface_part", EX_INTERFACE_PART);
        regHandler.item("pattern_provider_upgrade", PATTERN_PROVIDER_UPGRADE);
        regHandler.item("interface_upgrade", INTERFACE_UPGRADE);
        regHandler.item("io_bus_upgrade", IO_BUS_UPGRADE);
        regHandler.item("infinity_cell", INFINITY_CELL);
        regHandler.item("ex_export_bus_part", EX_EXPORT_BUS);
        regHandler.item("ex_import_bus_part", EX_IMPORT_BUS);
    }

}
