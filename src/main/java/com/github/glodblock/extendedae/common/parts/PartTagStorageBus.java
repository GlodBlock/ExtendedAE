package com.github.glodblock.extendedae.common.parts;

import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.core.AppEng;
import appeng.items.parts.PartModels;
import appeng.parts.PartModel;
import appeng.util.SettingsFrom;
import appeng.util.prioritylist.IPartitionList;
import com.github.glodblock.extendedae.EAE;
import com.github.glodblock.extendedae.common.me.taglist.TagExpParser;
import com.github.glodblock.extendedae.common.me.taglist.TagPriorityList;
import com.github.glodblock.extendedae.common.parts.base.PartSpecialStorageBus;
import com.github.glodblock.extendedae.container.ContainerTagStorageBus;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public class PartTagStorageBus extends PartSpecialStorageBus {

    public static final ResourceLocation MODEL_BASE = new ResourceLocation(EAE.MODID, "part/tag_storage_bus_base");

    @PartModels
    public static final IPartModel MODELS_OFF = new PartModel(MODEL_BASE, new ResourceLocation(AppEng.MOD_ID, "part/storage_bus_off"));

    @PartModels
    public static final IPartModel MODELS_ON = new PartModel(MODEL_BASE, new ResourceLocation(AppEng.MOD_ID, "part/storage_bus_on"));

    @PartModels
    public static final IPartModel MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE, new ResourceLocation(AppEng.MOD_ID, "part/storage_bus_has_channel"));

    private String oreExp = "";

    public PartTagStorageBus(IPartItem<?> partItem) {
        super(partItem);
    }

    @Override
    public void readFromNBT(CompoundTag data) {
        super.readFromNBT(data);
        this.oreExp = data.getString("oreExp");
    }

    @Override
    public void writeToNBT(CompoundTag data) {
        super.writeToNBT(data);
        data.putString("oreExp", this.oreExp);
    }

    public MenuType<?> getMenuType() {
        return ContainerTagStorageBus.TYPE;
    }

    @Override
    protected final int getUpgradeSlots() {
        return 2;
    }

    public void setTagFilter(String exp) {
        if (!exp.equals(this.oreExp)) {
            this.oreExp = exp;
            this.filter = null;
            this.forceUpdate();
        }
    }

    public String getTagFilter() {
        return this.oreExp;
    }

    protected IPartitionList createFilter() {
        if (this.filter == null) {
            this.filter = new TagPriorityList(TagExpParser.getMatchingOre(this.oreExp), this.oreExp);
        }
        return this.filter;
    }

    @Override
    public void importSettings(SettingsFrom mode, CompoundTag input, @Nullable Player player) {
        super.importSettings(mode, input, player);
        if (input.contains("ore_dict_exp")) {
            this.oreExp = input.getString("ore_dict_exp");
        } else {
            this.oreExp = "";
        }
    }

    @Override
    public void exportSettings(SettingsFrom mode, CompoundTag output) {
        super.exportSettings(mode, output);
        if (mode == SettingsFrom.MEMORY_CARD) {
            output.putString("ore_dict_exp", this.oreExp);
        }
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
