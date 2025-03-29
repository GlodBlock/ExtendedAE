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
import com.glodblock.github.extendedae.common.me.taglist.TagExpParser;
import com.glodblock.github.extendedae.common.me.taglist.TagPriorityList;
import com.glodblock.github.extendedae.common.me.taglist.TagStackTransferContext;
import com.glodblock.github.extendedae.common.parts.base.PartSpecialExportBus;
import com.glodblock.github.extendedae.container.ContainerTagExportBus;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
public class PartTagExportBus extends PartSpecialExportBus {
    private static final Logger LOGGER = LoggerFactory.getLogger("ExtendedAE-TagFilter");
    private static final boolean DEBUG_ENABLED = true; // Set to false to disable logging

    public static final ResourceLocation MODEL_BASE = ResourceLocation.fromNamespaceAndPath(ExtendedAE.MODID, "part/tag_export_bus_base");

    @PartModels
    public static final IPartModel MODELS_OFF = new PartModel(MODEL_BASE, ResourceLocation.fromNamespaceAndPath(AppEng.MOD_ID, "part/export_bus_off"));

    @PartModels
    public static final IPartModel MODELS_ON = new PartModel(MODEL_BASE, ResourceLocation.fromNamespaceAndPath(AppEng.MOD_ID, "part/export_bus_on"));

    @PartModels
    public static final IPartModel MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE, ResourceLocation.fromNamespaceAndPath(AppEng.MOD_ID, "part/export_bus_has_channel"));

    private String oreExpWhite = "";
    private String oreExpBlack = "";

    public PartTagExportBus(IPartItem<?> partItem) {
        super(partItem);
    }

    @Override
    public void readFromNBT(CompoundTag extra, HolderLookup.Provider registries) {
        super.readFromNBT(extra, registries);
        this.oreExpWhite = extra.getString("oreExp");
        this.oreExpBlack = extra.getString("oreExp2");
        
        if (DEBUG_ENABLED) {
            LOGGER.info("TagExportBus loaded from NBT with whitelist: '{}', blacklist: '{}'", 
                this.oreExpWhite, this.oreExpBlack);
        }
    }

    @Override
    public void writeToNBT(CompoundTag extra, HolderLookup.Provider registries) {
        super.writeToNBT(extra, registries);
        extra.putString("oreExp", this.oreExpWhite);
        extra.putString("oreExp2", this.oreExpBlack);
    }

    @Override
    public void importSettings(SettingsFrom mode, DataComponentMap input, @Nullable Player player) {
        super.importSettings(mode, input, player);
        var oreExps = input.get(EAESingletons.TAG_EXPRESS);
        if (oreExps != null) {
            this.oreExpWhite = oreExps.left();
            this.oreExpBlack = oreExps.right();
            
            if (DEBUG_ENABLED) {
                LOGGER.info("TagExportBus imported settings with whitelist: '{}', blacklist: '{}'", 
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

    public String getTagFilter(boolean isWhite) {
        return isWhite ? this.oreExpWhite : this.oreExpBlack;
    }

    public void setTagFilter(String exp, boolean isWhite) {
        if (isWhite) {
            if (!exp.equals(this.oreExpWhite)) {
                if (DEBUG_ENABLED) {
                    LOGGER.info("TagExportBus whitelist changed from '{}' to '{}'", this.oreExpWhite, exp);
                }
                this.oreExpWhite = exp;
                this.filter = null;
            }
        } else {
            if (!exp.equals(this.oreExpBlack)) {
                if (DEBUG_ENABLED) {
                    LOGGER.info("TagExportBus blacklist changed from '{}' to '{}'", this.oreExpBlack, exp);
                }
                this.oreExpBlack = exp;
                this.filter = null;
            }
        }
    }

    @Override
    protected MenuType<?> getMenuType() {
        return ContainerTagExportBus.TYPE;
    }

    @NotNull
    protected StackTransferContext createTransferContext(IStorageService storageService, IEnergyService energyService) {
        if (DEBUG_ENABLED) {
            LOGGER.info("Creating TagStackTransferContext for TagExportBus");
        }
        return new TagStackTransferContext(
                storageService,
                energyService,
                this.source,
                getOperationsPerTick(),
                createFilter()
        );
    }

    @Override
    protected IPartitionList createFilter() {
        if (DEBUG_ENABLED) {
            LOGGER.info("Creating filter for TagExportBus");
        }
        
        if (this.filter == null) {
            // Handle special cases or empty filter expressions
            String whitelist = this.oreExpWhite.trim();
            String blacklist = this.oreExpBlack.trim();
            
            if (DEBUG_ENABLED) {
                LOGGER.info("Creating new filter with whitelist: '{}', blacklist: '{}'", whitelist, blacklist);
            }
            
            // Handle specific cases where we know the filter should match nothing
            boolean emptyFilter = whitelist.isEmpty() && blacklist.isEmpty();
            boolean operatorOnly = !whitelist.isEmpty() && 
                                   (whitelist.equals("&") || whitelist.equals("|") || whitelist.equals("^") ||
                                    whitelist.matches("^\\s*[&|^].*") || whitelist.matches(".*[&|^]\\s*$"));
            
            if (DEBUG_ENABLED) {
                LOGGER.info("Filter analysis: emptyFilter={}, operatorOnly={}", emptyFilter, operatorOnly);
                
                if (whitelist.contains("&")) {
                    LOGGER.info("Expression contains '&', parsed as: {}", 
                        TagExpParser.parseExpression(whitelist).getClass().getSimpleName());
                }
            }
                                    
            if (operatorOnly) {
                // Create a filter that matches nothing
                if (DEBUG_ENABLED) {
                    LOGGER.info("Creating empty filter that matches nothing");
                }
                return new TagPriorityList(Set.of(), Set.of(), whitelist);
            } else {
                // For AND expressions, we need to create a specialized filter
                if (whitelist.contains("&") && 
                    TagExpParser.parseExpression(whitelist) instanceof TagExpParser.AndExpression) {
                    if (DEBUG_ENABLED) {
                        LOGGER.info("Creating AND filter with expression: {}", whitelist);
                    }
                    // For AND expressions, whitelist will be empty but we pass the expression for evaluation
                    this.filter = new TagPriorityList(
                        Set.of(), // Empty whitelist for AND expressions
                        TagExpParser.getMatchingOre(blacklist),
                        whitelist + (blacklist.isEmpty() ? "" : ";" + blacklist)
                    );
                } else {
                    // Standard filter creation
                    if (DEBUG_ENABLED) {
                        LOGGER.info("Creating standard filter");
                    }
                    this.filter = new TagPriorityList(
                        TagExpParser.getMatchingOre(whitelist),
                        TagExpParser.getMatchingOre(blacklist),
                        whitelist + (blacklist.isEmpty() ? "" : ";" + blacklist)
                    );
                }
            }
        } else {
            if (DEBUG_ENABLED) {
                LOGGER.info("Using existing filter");
            }
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
