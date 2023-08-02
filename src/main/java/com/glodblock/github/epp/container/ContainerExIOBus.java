package com.glodblock.github.epp.container;

import appeng.menu.SlotSemantic;
import appeng.menu.SlotSemantics;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.UpgradeableMenu;
import appeng.menu.slot.FakeSlot;
import appeng.menu.slot.OptionalFakeSlot;
import appeng.parts.automation.IOBusPart;
import appeng.util.ConfigMenuInventory;
import com.glodblock.github.epp.common.parts.PartExExportBus;
import com.glodblock.github.epp.common.parts.PartExImportBus;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerType;

public class ContainerExIOBus extends UpgradeableMenu<IOBusPart> {

    public static final ScreenHandlerType<ContainerExIOBus> EXPORT_TYPE = MenuTypeBuilder
            .create(ContainerExIOBus::new, PartExExportBus.class)
            .build("ex_export_bus");

    public static final ScreenHandlerType<ContainerExIOBus> IMPORT_TYPE = MenuTypeBuilder
            .create(ContainerExIOBus::new, PartExImportBus.class)
            .build("ex_import_bus");

    public ContainerExIOBus(ScreenHandlerType<?> menuType, int id, PlayerInventory ip, IOBusPart host) {
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
