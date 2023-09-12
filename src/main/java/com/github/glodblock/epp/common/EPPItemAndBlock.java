package com.github.glodblock.epp.common;

import appeng.items.parts.PartItem;
import com.github.glodblock.epp.common.blocks.BlockExDrive;
import com.github.glodblock.epp.common.blocks.BlockExInterface;
import com.github.glodblock.epp.common.blocks.BlockExPatternProvider;
import com.github.glodblock.epp.common.blocks.BlockIngredientBuffer;
import com.github.glodblock.epp.common.items.*;
import com.github.glodblock.epp.common.parts.PartExExportBus;
import com.github.glodblock.epp.common.parts.PartExImportBus;
import com.github.glodblock.epp.common.parts.PartExInterface;
import com.github.glodblock.epp.common.parts.PartExPatternProvider;
import com.github.glodblock.epp.common.tileentities.TileExDrive;
import com.github.glodblock.epp.common.tileentities.TileExInterface;
import com.github.glodblock.epp.common.tileentities.TileExPatternProvider;
import com.github.glodblock.epp.common.tileentities.TileIngredientBuffer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class EPPItemAndBlock {

    public static final CreativeModeTab TAB = new CreativeModeTab("epp") {
        @Nonnull
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(EX_PATTERN_PROVIDER);
        }
    };

    public static BlockExPatternProvider EX_PATTERN_PROVIDER;
    public static PartItem<PartExPatternProvider> EX_PATTERN_PROVIDER_PART;
    public static BlockExInterface EX_INTERFACE;
    public static PartItem<PartExInterface> EX_INTERFACE_PART;
    public static InfinityCell INFINITY_CELL;
    public static PartItem<PartExExportBus> EX_EXPORT_BUS;
    public static PartItem<PartExImportBus> EX_IMPORT_BUS;
    public static ItemPatternProviderUpgrade PATTERN_PROVIDER_UPGRADE;
    public static ItemInterfaceUpgrade INTERFACE_UPGRADE;
    public static ItemIOBusUpgrade IO_BUS_UPGRADE;
    public static ItemMEPackingTape PACKING_TAPE;
    public static ItemPackedDevice PACKAGE;
    public static BlockExDrive EX_DRIVE;
    public static ItemDriveUpgrade DRIVE_UPGRADE;
    public static BlockIngredientBuffer INGREDIENT_BUFFER;

    public static void init(RegistryHandler regHandler) {
        EX_PATTERN_PROVIDER = new BlockExPatternProvider();
        EX_PATTERN_PROVIDER_PART = new PartItem<>(new Item.Properties().tab(TAB), PartExPatternProvider.class, PartExPatternProvider::new);
        EX_INTERFACE = new BlockExInterface();
        EX_INTERFACE_PART = new PartItem<>(new Item.Properties().tab(TAB), PartExInterface.class, PartExInterface::new);
        INFINITY_CELL = new InfinityCell();
        EX_EXPORT_BUS = new PartItem<>(new Item.Properties().tab(TAB), PartExExportBus.class, PartExExportBus::new);
        EX_IMPORT_BUS = new PartItem<>(new Item.Properties().tab(TAB), PartExImportBus.class, PartExImportBus::new);
        IO_BUS_UPGRADE = new ItemIOBusUpgrade();
        INTERFACE_UPGRADE = new ItemInterfaceUpgrade();
        PATTERN_PROVIDER_UPGRADE = new ItemPatternProviderUpgrade();
        PACKING_TAPE = new ItemMEPackingTape();
        PACKAGE = new ItemPackedDevice();
        EX_DRIVE = new BlockExDrive();
        DRIVE_UPGRADE = new ItemDriveUpgrade();
        INGREDIENT_BUFFER = new BlockIngredientBuffer();
        regHandler.block("ex_pattern_provider", EX_PATTERN_PROVIDER, TileExPatternProvider.class, TileExPatternProvider::new);
        regHandler.block("ex_interface", EX_INTERFACE, TileExInterface.class, TileExInterface::new);
        regHandler.block("ex_drive", EX_DRIVE, TileExDrive.class, TileExDrive::new);
        regHandler.block("ingredient_buffer", INGREDIENT_BUFFER, TileIngredientBuffer.class, TileIngredientBuffer::new);
        regHandler.item("ex_pattern_provider_part", EX_PATTERN_PROVIDER_PART);
        regHandler.item("ex_interface_part", EX_INTERFACE_PART);
        regHandler.item("pattern_provider_upgrade", PATTERN_PROVIDER_UPGRADE);
        regHandler.item("interface_upgrade", INTERFACE_UPGRADE);
        regHandler.item("io_bus_upgrade", IO_BUS_UPGRADE);
        regHandler.item("infinity_cell", INFINITY_CELL);
        regHandler.item("ex_export_bus_part", EX_EXPORT_BUS);
        regHandler.item("ex_import_bus_part", EX_IMPORT_BUS);
        regHandler.item("me_packing_tape", PACKING_TAPE);
        regHandler.item("package", PACKAGE);
        regHandler.item("drive_upgrade", DRIVE_UPGRADE);
    }

}
