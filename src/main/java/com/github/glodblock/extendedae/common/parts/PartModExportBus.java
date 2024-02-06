package com.github.glodblock.extendedae.common.parts;

import appeng.api.behaviors.StackTransferContext;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.storage.IStorageService;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.core.AppEng;
import appeng.items.parts.PartModels;
import appeng.parts.PartModel;
import appeng.util.prioritylist.IPartitionList;
import com.github.glodblock.extendedae.EAE;
import com.github.glodblock.extendedae.common.me.modlist.ModPriorityList;
import com.github.glodblock.extendedae.common.me.modlist.ModStackTransferContext;
import com.github.glodblock.extendedae.common.parts.base.PartSpecialExportBus;
import com.github.glodblock.extendedae.container.ContainerModExportBus;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class PartModExportBus extends PartSpecialExportBus {

    public static final ResourceLocation MODEL_BASE = new ResourceLocation(EAE.MODID, "part/mod_export_bus_base");

    @PartModels
    public static final IPartModel MODELS_OFF = new PartModel(MODEL_BASE,
            new ResourceLocation(AppEng.MOD_ID, "part/export_bus_off"));

    @PartModels
    public static final IPartModel MODELS_ON = new PartModel(MODEL_BASE,
            new ResourceLocation(AppEng.MOD_ID, "part/export_bus_on"));

    @PartModels
    public static final IPartModel MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE,
            new ResourceLocation(AppEng.MOD_ID, "part/export_bus_has_channel"));

    private String modid = "";

    public PartModExportBus(IPartItem<?> partItem) {
        super(partItem);
    }

    @Override
    public void readFromNBT(CompoundTag extra) {
        super.readFromNBT(extra);
        this.modid = extra.getString("modid");
    }

    @Override
    public void writeToNBT(CompoundTag extra) {
        super.writeToNBT(extra);
        extra.putString("modid", this.modid);
    }

    public String getModNameFilter() {
        return this.modid;
    }

    public void setModNameFilter(String exp) {
        if (!exp.equals(this.modid)) {
            this.modid = exp;
            this.filter = null;
        }
    }

    @Override
    protected MenuType<?> getMenuType() {
        return ContainerModExportBus.TYPE;
    }

    @NotNull
    protected StackTransferContext createTransferContext(IStorageService storageService, IEnergyService energyService) {
        return new ModStackTransferContext(
                storageService,
                energyService,
                this.source,
                getOperationsPerTick(),
                createFilter()
        );
    }

    @Override
    protected IPartitionList createFilter() {
        if (this.filter == null) {
            this.filter = new ModPriorityList(this.modid);
        }
        return this.filter;
    }

    @Override
    protected final int getUpgradeSlots() {
        return 4;
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
}
