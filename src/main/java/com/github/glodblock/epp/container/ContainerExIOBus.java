package com.github.glodblock.epp.container;

import appeng.menu.SlotSemantic;
import appeng.menu.SlotSemantics;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.UpgradeableMenu;
import appeng.menu.slot.FakeSlot;
import appeng.menu.slot.OptionalFakeSlot;
import appeng.parts.automation.IOBusPart;
import appeng.util.ConfigMenuInventory;
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
        ConfigMenuInventory inv = this.getHost().getConfig().createMenuWrapper();
        SlotSemantic s = SlotSemantics.CONFIG;
        this.addSlot(new FakeSlot(inv, 0), s);
        this.addSlot(new OptionalFakeSlot(inv, this, 1, 1), s);
        this.addSlot(new OptionalFakeSlot(inv, this, 2, 1), s);
        this.addSlot(new OptionalFakeSlot(inv, this, 3, 1), s);
        this.addSlot(new OptionalFakeSlot(inv, this, 4, 1), s);
        this.addSlot(new OptionalFakeSlot(inv, this, 5, 2), s);
        this.addSlot(new OptionalFakeSlot(inv, this, 6, 2), s);
        this.addSlot(new OptionalFakeSlot(inv, this, 7, 2), s);
        this.addSlot(new OptionalFakeSlot(inv, this, 8, 2), s);
    }
}
