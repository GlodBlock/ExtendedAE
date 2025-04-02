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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class PartTagStorageBus extends PartSpecialStorageBus {
    private static final Logger LOGGER = LoggerFactory.getLogger("ExtendedAE-TagFilter");
    private static final boolean DEBUG_ENABLED = true; // Set to false to disable logging

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
        
        if (DEBUG_ENABLED) {
            LOGGER.info("TagStorageBus loaded from NBT with whitelist: '{}', blacklist: '{}'", 
                this.oreExpWhite, this.oreExpBlack);
        }
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
                if (DEBUG_ENABLED) {
                    LOGGER.info("TagStorageBus whitelist changed from '{}' to '{}'", this.oreExpWhite, exp);
                }
                this.oreExpWhite = exp;
                this.filter = null;
                this.forceUpdate();
            }
        } else {
            if (!exp.equals(this.oreExpBlack)) {
                if (DEBUG_ENABLED) {
                    LOGGER.info("TagStorageBus blacklist changed from '{}' to '{}'", this.oreExpBlack, exp);
                }
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
        if (DEBUG_ENABLED) {
            LOGGER.info("Creating filter for TagStorageBus");
        }
        
        if (this.filter == null) {
            this.filter = new TagPriorityList(this.oreExpWhite, this.oreExpBlack);
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
            
            if (DEBUG_ENABLED) {
                LOGGER.info("TagStorageBus imported settings with whitelist: '{}', blacklist: '{}'", 
                    this.oreExpWhite, this.oreExpBlack);
            }
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
