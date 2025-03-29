package com.glodblock.github.extendedae.common.me.taglist;

import appeng.api.behaviors.StackTransferContext;
import appeng.api.config.Actionable;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageService;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.util.prioritylist.IPartitionList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// clueless
@SuppressWarnings({"UnstableApiUsage", "NonExtendableApiUsage"})
public class TagStackTransferContext implements StackTransferContext {
    private static final Logger LOGGER = LoggerFactory.getLogger("ExtendedAE-TagFilter");
    private static final boolean DEBUG_ENABLED = true; // Set to false to disable logging

    private final IStorageService internalStorage;
    private final IEnergySource energySource;
    private final IActionSource actionSource;
    private final IPartitionList filter;
    private final int initialOperations;
    private long operationsRemaining;
    private boolean isInverted;

    public TagStackTransferContext(IStorageService internalStorage, IEnergySource energySource, IActionSource actionSource, int operationsRemaining, IPartitionList filter) {
        this.internalStorage = internalStorage;
        this.energySource = energySource;
        this.actionSource = actionSource;
        this.filter = filter;
        this.initialOperations = operationsRemaining;
        this.operationsRemaining = operationsRemaining;
        
        if (DEBUG_ENABLED) {
            LOGGER.info("Created TagStackTransferContext with filter: {}", filter.getClass().getSimpleName());
            LOGGER.info("Filter isEmpty: {}", filter.isEmpty());
        }
    }

    @Override
    public IStorageService getInternalStorage() {
        return internalStorage;
    }

    @Override
    public IEnergySource getEnergySource() {
        return energySource;
    }

    @Override
    public IActionSource getActionSource() {
        return actionSource;
    }

    @Override
    public int getOperationsRemaining() {
        return (int) operationsRemaining;
    }

    @Override
    public void setOperationsRemaining(int operationsRemaining) {
        this.operationsRemaining = operationsRemaining;
    }

    @Override
    public boolean hasOperationsLeft() {
        return operationsRemaining > 0;
    }

    @Override
    public boolean hasDoneWork() {
        return initialOperations > operationsRemaining;
    }

    @Override
    public boolean isKeyTypeEnabled(AEKeyType space) {
        return space == AEKeyType.items() || space == AEKeyType.fluids();
    }

    @Override
    public boolean isInFilter(AEKey key) {
        boolean isEmpty = filter.isEmpty();
        boolean isListed = isEmpty || filter.isListed(key);
        
        if (DEBUG_ENABLED) {
            LOGGER.info("isInFilter check for item {}: isEmpty={}, isListed={}, final result={}", 
                key, isEmpty, isListed, isListed);
        }
        
        return isListed;
    }

    @Override
    public IPartitionList getFilter() {
        return filter;
    }

    @Override
    public void setInverted(boolean inverted) {
        if (DEBUG_ENABLED && this.isInverted != inverted) {
            LOGGER.info("Filter inversion changed to: {}", inverted);
        }
        this.isInverted = inverted;
    }

    @Override
    public boolean isInverted() {
        boolean result = !filter.isEmpty() && isInverted;
        if (DEBUG_ENABLED) {
            LOGGER.info("isInverted check: !filter.isEmpty()={}, isInverted={}, result={}", 
                !filter.isEmpty(), isInverted, result);
        }
        return result;
    }

    @Override
    public boolean canInsert(AEItemKey what, long amount) {
        boolean canInsert = internalStorage.getInventory().insert(
                what,
                amount,
                Actionable.SIMULATE,
                actionSource) > 0;
                
        if (DEBUG_ENABLED) {
            LOGGER.info("canInsert check for item {}: {}", what, canInsert);
        }
        
        return canInsert;
    }

    @Override
    public void reduceOperationsRemaining(long inserted) {
        if (DEBUG_ENABLED) {
            LOGGER.info("Reducing operations remaining by {}: {} -> {}", 
                inserted, operationsRemaining, operationsRemaining - inserted);
        }
        operationsRemaining -= inserted;
    }
}
