package com.glodblock.github.extendedae.common.parts;

import appeng.api.behaviors.StackExportStrategy;
import appeng.api.behaviors.StackTransferContext;
import appeng.api.networking.IGrid;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.storage.IStorageService;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.core.AppEng;
import appeng.core.settings.TickRates;
import appeng.items.parts.PartModels;
import appeng.parts.PartModel;
import appeng.parts.automation.IOBusPart;
import appeng.parts.automation.StackWorldBehaviors;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.me.taglist.TagExpParser;
import com.glodblock.github.extendedae.common.me.taglist.TagPriorityList;
import com.glodblock.github.extendedae.common.me.taglist.TagStackTransferContext;
import com.glodblock.github.extendedae.container.ContainerTagExportBus;
import com.google.common.collect.ImmutableList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PartTagExportBus extends IOBusPart {

    public static final ResourceLocation MODEL_BASE = new ResourceLocation(ExtendedAE.MODID, "part/tag_export_bus_base");

    @PartModels
    public static final IPartModel MODELS_OFF = new PartModel(MODEL_BASE,
            new ResourceLocation(AppEng.MOD_ID, "part/export_bus_off"));

    @PartModels
    public static final IPartModel MODELS_ON = new PartModel(MODEL_BASE,
            new ResourceLocation(AppEng.MOD_ID, "part/export_bus_on"));

    @PartModels
    public static final IPartModel MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE,
            new ResourceLocation(AppEng.MOD_ID, "part/export_bus_has_channel"));
    @Nullable
    private StackExportStrategy exportStrategy;
    private String oreExp = "";
    private TagPriorityList filter = null;

    public PartTagExportBus(IPartItem<?> partItem) {
        super(TickRates.ExportBus, StackWorldBehaviors.hasExportStrategyFilter(), partItem);
    }

    @Override
    public void readFromNBT(CompoundTag extra) {
        super.readFromNBT(extra);
        this.oreExp = extra.getString("oreExp");
    }

    @Override
    public void writeToNBT(CompoundTag extra) {
        super.writeToNBT(extra);
        extra.putString("oreExp", this.oreExp);
    }

    protected final StackExportStrategy getExportStrategy() {
        if (exportStrategy == null) {
            var self = this.getHost().getBlockEntity();
            var fromPos = self.getBlockPos().relative(this.getSide());
            var fromSide = getSide().getOpposite();
            exportStrategy = StackWorldBehaviors.createExportFacade((ServerLevel) getLevel(), fromPos, fromSide);
        }
        return exportStrategy;
    }

    public String getTagFilter() {
        return this.oreExp;
    }

    public void setTagFilter(String exp) {
        if (!exp.equals(this.oreExp)) {
            this.oreExp = exp;
            this.filter = null;
        }
    }

    @Override
    protected MenuType<?> getMenuType() {
        return ContainerTagExportBus.TYPE;
    }

    @Override
    protected boolean doBusWork(IGrid grid) {
        var storageService = grid.getStorageService();
        var filter = this.createFilter();
        var context = createTransferContext(storageService, grid.getEnergyService());
        for (var what : ImmutableList.copyOf(storageService.getCachedInventory())) {
            if (!filter.isListed(what.getKey())) {
                continue;
            }
            var transferFactory = what.getKey().getAmountPerOperation();
            long amount = (long) context.getOperationsRemaining() * transferFactory;
            amount = getExportStrategy().transfer(context, what.getKey(), amount);
            context.reduceOperationsRemaining(Math.max(1, amount / transferFactory));
            if (!context.hasOperationsLeft()) {
                break;
            }
        }
        return context.hasDoneWork();
    }

    @NotNull
    private StackTransferContext createTransferContext(IStorageService storageService, IEnergyService energyService) {
        return new TagStackTransferContext(
                storageService,
                energyService,
                this.source,
                getOperationsPerTick(),
                createFilter()
        );
    }

    private TagPriorityList createFilter() {
        if (this.filter == null) {
            this.filter = new TagPriorityList(TagExpParser.getMatchingOre(this.oreExp), this.oreExp);
        }
        return this.filter;
    }

    @Override
    protected final int getUpgradeSlots() {
        return 4;
    }

    @Override
    public void getBoxes(IPartCollisionHelper bch) {
        bch.addBox(4, 4, 12, 12, 12, 14);
        bch.addBox(5, 5, 14, 11, 11, 15);
        bch.addBox(6, 6, 15, 10, 10, 16);
        bch.addBox(6, 6, 11, 10, 10, 12);
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
