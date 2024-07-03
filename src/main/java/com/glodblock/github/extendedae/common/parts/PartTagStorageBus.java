package com.glodblock.github.extendedae.common.parts;

import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.core.AppEng;
import appeng.items.parts.PartModels;
import appeng.parts.PartModel;
import appeng.util.SettingsFrom;
import appeng.util.prioritylist.IPartitionList;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.common.me.taglist.TagExpParser;
import com.glodblock.github.extendedae.common.me.taglist.TagPriorityList;
import com.glodblock.github.extendedae.common.parts.base.PartSpecialStorageBus;
import com.glodblock.github.extendedae.container.ContainerTagStorageBus;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public class PartTagStorageBus extends PartSpecialStorageBus {

    public static final ResourceLocation MODEL_BASE = ResourceLocation.fromNamespaceAndPath(ExtendedAE.MODID, "part/tag_storage_bus_base");

    @PartModels
    public static final IPartModel MODELS_OFF = new PartModel(MODEL_BASE, ResourceLocation.fromNamespaceAndPath(AppEng.MOD_ID, "part/storage_bus_off"));

    @PartModels
    public static final IPartModel MODELS_ON = new PartModel(MODEL_BASE, ResourceLocation.fromNamespaceAndPath(AppEng.MOD_ID, "part/storage_bus_on"));

    @PartModels
    public static final IPartModel MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE, ResourceLocation.fromNamespaceAndPath(AppEng.MOD_ID, "part/storage_bus_has_channel"));

    private String oreExpWhite = "";
    private String oreExpBlack = "";

    public PartTagStorageBus(IPartItem<?> partItem) {
        super(partItem);
    }

    @Override
    public void readFromNBT(CompoundTag data, HolderLookup.Provider registries) {
        super.readFromNBT(data, registries);
        this.oreExpWhite = data.getString("oreExp");
        this.oreExpBlack = data.getString("oreExp2");
    }

    @Override
    public void writeToNBT(CompoundTag data, HolderLookup.Provider registries) {
        super.writeToNBT(data, registries);
        data.putString("oreExp", this.oreExpWhite);
        data.putString("oreExp2", this.oreExpBlack);
    }

    public MenuType<?> getMenuType() {
        return ContainerTagStorageBus.TYPE;
    }

    @Override
    protected final int getUpgradeSlots() {
        return 2;
    }

    public void setTagFilter(String exp, boolean isWhite) {
        if (isWhite) {
            if (!exp.equals(this.oreExpWhite)) {
                this.oreExpWhite = exp;
                this.filter = null;
                this.forceUpdate();
            }
        } else {
            if (!exp.equals(this.oreExpBlack)) {
                this.oreExpBlack = exp;
                this.filter = null;
                this.forceUpdate();
            }
        }
    }

    public String getTagFilter(boolean isWhite) {
        return isWhite ? this.oreExpWhite : this.oreExpBlack;
    }

    protected IPartitionList createFilter() {
        if (this.filter == null) {
            this.filter = new TagPriorityList(TagExpParser.getMatchingOre(this.oreExpWhite), TagExpParser.getMatchingOre(this.oreExpBlack), this.oreExpWhite + this.oreExpBlack);
        }
        return this.filter;
    }

    @Override
    public void importSettings(SettingsFrom mode, DataComponentMap input, @Nullable Player player) {
        super.importSettings(mode, input, player);
        var oreExps = input.get(EAESingletons.TAG_EXPRESS);
        if (oreExps != null) {
            this.oreExpWhite = oreExps.left();
            this.oreExpBlack = oreExps.right();
        }
    }

    @Override
    public void exportSettings(SettingsFrom mode, DataComponentMap.Builder output) {
        super.exportSettings(mode, output);
        if (mode == SettingsFrom.MEMORY_CARD) {
            output.set(EAESingletons.TAG_EXPRESS, Pair.of(this.oreExpWhite, this.oreExpBlack));
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
