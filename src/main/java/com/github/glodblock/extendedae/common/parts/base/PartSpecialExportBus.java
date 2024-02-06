package com.github.glodblock.extendedae.common.parts.base;

import appeng.api.behaviors.StackExportStrategy;
import appeng.api.behaviors.StackTransferContext;
import appeng.api.networking.IGrid;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.storage.IStorageService;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartItem;
import appeng.core.settings.TickRates;
import appeng.parts.automation.IOBusPart;
import appeng.parts.automation.StackWorldBehaviors;
import appeng.util.prioritylist.IPartitionList;
import com.google.common.collect.ImmutableList;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public abstract class PartSpecialExportBus extends IOBusPart {

    @Nullable
    protected StackExportStrategy exportStrategy;
    protected IPartitionList filter = null;

    public PartSpecialExportBus(IPartItem<?> partItem) {
        super(TickRates.ExportBus, StackWorldBehaviors.hasExportStrategyFilter(), partItem);
    }

    protected StackExportStrategy getExportStrategy() {
        if (exportStrategy == null) {
            var self = this.getHost().getBlockEntity();
            var fromPos = self.getBlockPos().relative(this.getSide());
            var fromSide = getSide().getOpposite();
            exportStrategy = StackWorldBehaviors.createExportFacade((ServerLevel) getLevel(), fromPos, fromSide);
        }
        return exportStrategy;
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

    @Override
    public void getBoxes(IPartCollisionHelper bch) {
        bch.addBox(4, 4, 12, 12, 12, 14);
        bch.addBox(5, 5, 14, 11, 11, 15);
        bch.addBox(6, 6, 15, 10, 10, 16);
        bch.addBox(6, 6, 11, 10, 10, 12);
    }

    protected abstract StackTransferContext createTransferContext(IStorageService storageService, IEnergyService energyService);

    protected abstract IPartitionList createFilter();

}
