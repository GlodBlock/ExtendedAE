package com.glodblock.github.extendedae.common.parts;

import appeng.api.parts.IPartItem;
import appeng.parts.automation.ImportBusPart;
import com.glodblock.github.extendedae.config.EAEConfig;
import com.glodblock.github.extendedae.container.ContainerExIOBus;
import net.minecraft.world.inventory.MenuType;

public class PartExImportBus extends ImportBusPart {

    public PartExImportBus(IPartItem<?> partItem) {
        super(partItem);
    }

    @Override
    protected int getOperationsPerTick() {
        return super.getOperationsPerTick() * EAEConfig.busSpeed;
    }

    @Override
    protected int getUpgradeSlots() {
        return 8;
    }

    @Override
    protected MenuType<?> getMenuType() {
        return ContainerExIOBus.IMPORT_TYPE;
    }

}
