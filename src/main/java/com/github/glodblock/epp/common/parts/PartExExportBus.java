package com.github.glodblock.epp.common.parts;

import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.core.AppEngBase;
import appeng.parts.PartModel;
import appeng.parts.automation.ExportBusPart;
import com.github.glodblock.epp.EPP;
import com.github.glodblock.epp.container.ContainerExIOBus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;

import java.util.Arrays;
import java.util.List;

public class PartExExportBus extends ExportBusPart {

    public static List<ResourceLocation> MODELS = Arrays.asList(
            new ResourceLocation(EPP.MODID, "part/ex_export_bus_base"),
            new ResourceLocation(AppEngBase.MOD_ID, "part/export_bus_on"),
            new ResourceLocation(AppEngBase.MOD_ID, "part/export_bus_off"),
            new ResourceLocation(AppEngBase.MOD_ID, "part/export_bus_has_channel")
    );

    public static final PartModel MODELS_OFF = new PartModel(MODELS.get(0), MODELS.get(2));
    public static final PartModel MODELS_ON = new PartModel(MODELS.get(0), MODELS.get(1));
    public static final PartModel MODELS_HAS_CHANNEL = new PartModel(MODELS.get(0), MODELS.get(3));

    public PartExExportBus(IPartItem<?> partItem) {
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
        return super.getOperationsPerTick() * 8;
    }

    @Override
    protected MenuType<?> getMenuType() {
        return ContainerExIOBus.EXPORT_TYPE;
    }

}
