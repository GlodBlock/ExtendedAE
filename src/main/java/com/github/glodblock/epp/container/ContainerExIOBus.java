package com.github.glodblock.epp.container;

import appeng.core.definitions.AEItems;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.UpgradeableMenu;
import appeng.parts.automation.IOBusPart;
import com.github.glodblock.epp.common.parts.PartExExportBus;
import com.github.glodblock.epp.common.parts.PartExImportBus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class ContainerExIOBus extends UpgradeableMenu<IOBusPart> {

    public static final MenuType<ContainerExIOBus> EXPORT_TYPE = MenuTypeBuilder
            .create(ContainerExIOBus::new, PartExExportBus.class)
            .build("ex_export_bus");

    public static final MenuType<ContainerExIOBus> IMPORT_TYPE = MenuTypeBuilder
            .create(ContainerExIOBus::new, PartExImportBus.class)
            .build("ex_import_bus");

    public ContainerExIOBus(MenuType<?> menuType, int id, Inventory ip, IOBusPart host) {
        super(menuType, id, ip, host);
    }

    @Override
    protected void setupConfig() {
        addExpandableConfigSlots(getHost().getConfig(), 2, 9, 5);
    }

    @Override
    public boolean isSlotEnabled(int idx) {
        final int upgrades = getUpgrades().getInstalledUpgrades(AEItems.CAPACITY_CARD);
        return upgrades > idx;
    }
}
