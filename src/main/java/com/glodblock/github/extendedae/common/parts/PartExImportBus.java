package com.glodblock.github.extendedae.common.parts;

import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.core.AppEngBase;
import appeng.parts.PartModel;
import appeng.parts.automation.ImportBusPart;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.config.EPPConfig;
import com.glodblock.github.extendedae.container.ContainerExIOBus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;

import java.util.Arrays;
import java.util.List;

public class PartExImportBus extends ImportBusPart {

    public static List<ResourceLocation> MODELS = Arrays.asList(
            new ResourceLocation(ExtendedAE.MODID, "part/ex_import_bus_base"),
            new ResourceLocation(AppEngBase.MOD_ID, "part/import_bus_on"),
            new ResourceLocation(AppEngBase.MOD_ID, "part/import_bus_off"),
            new ResourceLocation(AppEngBase.MOD_ID, "part/import_bus_has_channel")
    );

    public static final PartModel MODELS_OFF = new PartModel(MODELS.get(0), MODELS.get(2));
    public static final PartModel MODELS_ON = new PartModel(MODELS.get(0), MODELS.get(1));
    public static final PartModel MODELS_HAS_CHANNEL = new PartModel(MODELS.get(0), MODELS.get(3));

    public PartExImportBus(IPartItem<?> partItem) {
        super(partItem);
    }

    @Override
    public IPartModel getStaticModels() {
        if (this.isActive() && this.isPowered()) {
            return MODELS_HAS_CHANNEL;
        } else if (this.isPowered()) {
            return MODELS_ON;
        } else {
            return MODELS_OFF;
        }
    }

    @Override
    protected int getOperationsPerTick() {
        return super.getOperationsPerTick() * EPPConfig.busSpeed;
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
