package com.glodblock.github.extendedae.xmod.appliede.containers;

import appeng.menu.implementations.MenuTypeBuilder;
import com.glodblock.github.extendedae.container.ContainerExIOBus;
import com.glodblock.github.extendedae.xmod.appliede.parts.PartExEMCExportBus;
import com.glodblock.github.extendedae.xmod.appliede.parts.PartExEMCImportBus;
import net.minecraft.world.inventory.MenuType;

public class ContainerExEMCIOBus {

    public static final MenuType<ContainerExIOBus> EXPORT_TYPE = MenuTypeBuilder
            .create(ContainerExIOBus::new, PartExEMCExportBus.class)
            .build("ex_emc_export_bus");

    public static final MenuType<ContainerExIOBus> IMPORT_TYPE = MenuTypeBuilder
            .create(ContainerExIOBus::new, PartExEMCImportBus.class)
            .build("ex_emc_import_bus");

}
