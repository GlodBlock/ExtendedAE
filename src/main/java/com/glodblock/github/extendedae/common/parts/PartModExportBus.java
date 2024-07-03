package com.glodblock.github.extendedae.common.parts;

import appeng.api.behaviors.StackTransferContext;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.storage.IStorageService;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.core.AppEng;
import appeng.items.parts.PartModels;
import appeng.parts.PartModel;
import appeng.util.SettingsFrom;
import appeng.util.prioritylist.IPartitionList;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.common.me.modlist.ModPriorityList;
import com.glodblock.github.extendedae.common.me.modlist.ModStackTransferContext;
import com.glodblock.github.extendedae.common.parts.base.PartSpecialExportBus;
import com.glodblock.github.extendedae.container.ContainerModExportBus;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class PartModExportBus extends PartSpecialExportBus {

    public static final ResourceLocation MODEL_BASE = ResourceLocation.fromNamespaceAndPath(ExtendedAE.MODID, "part/mod_export_bus_base");

    @PartModels
    public static final IPartModel MODELS_OFF = new PartModel(MODEL_BASE, ResourceLocation.fromNamespaceAndPath(AppEng.MOD_ID, "part/export_bus_off"));

    @PartModels
    public static final IPartModel MODELS_ON = new PartModel(MODEL_BASE, ResourceLocation.fromNamespaceAndPath(AppEng.MOD_ID, "part/export_bus_on"));

    @PartModels
    public static final IPartModel MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE, ResourceLocation.fromNamespaceAndPath(AppEng.MOD_ID, "part/export_bus_has_channel"));

    private String modid = "";

    public PartModExportBus(IPartItem<?> partItem) {
        super(partItem);
    }

    @Override
    public void readFromNBT(CompoundTag extra, HolderLookup.Provider registries) {
        super.readFromNBT(extra, registries);
        this.modid = extra.getString("modid");
    }

    @Override
    public void writeToNBT(CompoundTag extra, HolderLookup.Provider registries) {
        super.writeToNBT(extra, registries);
        extra.putString("modid", this.modid);
    }

    @Override
    public void importSettings(SettingsFrom mode, DataComponentMap input, @Nullable Player player) {
        super.importSettings(mode, input, player);
        var id = input.get(EAESingletons.MOD_EXPRESS);
        if (id != null) {
            this.modid = id;
        }
    }

    @Override
    public void exportSettings(SettingsFrom mode, DataComponentMap.Builder output) {
        super.exportSettings(mode, output);
        if (mode == SettingsFrom.MEMORY_CARD) {
            output.set(EAESingletons.MOD_EXPRESS, this.modid);
        }
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
