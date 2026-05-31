package com.glodblock.github.extendedae.common.parts;

import appeng.api.behaviors.StackTransferContext;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.storage.IStorageService;
import appeng.api.parts.IPartItem;
import appeng.util.SettingsFrom;
import appeng.util.prioritylist.IPartitionList;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.common.me.modlist.ModPriorityList;
import com.glodblock.github.extendedae.common.me.modlist.ModStackTransferContext;
import com.glodblock.github.extendedae.common.parts.base.PartSpecialExportBus;
import com.glodblock.github.extendedae.container.ContainerModExportBus;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class PartModExportBus extends PartSpecialExportBus {

    private String modid = "";

    public PartModExportBus(IPartItem<?> partItem) {
        super(partItem);
    }

    @Override
    public void readFromNBT(ValueInput extra) {
        super.readFromNBT(extra);
        this.modid = extra.getStringOr("modid", "");
    }

    @Override
    public void writeToNBT(ValueOutput extra) {
        super.writeToNBT(extra);
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

}
