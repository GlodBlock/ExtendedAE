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
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.config.EAEConfig;

// clueless
@SuppressWarnings({"UnstableApiUsage", "NonExtendableApiUsage"})
public class TagStackTransferContext implements StackTransferContext {

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
        
        if (EAEConfig.debugMode) {
            ExtendedAE.LOGGER.debug("Created TagStackTransferContext with filter: {}", filter.getClass().getSimpleName());
            ExtendedAE.LOGGER.debug("Filter isEmpty: {}", filter.isEmpty());
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
        
        if (EAEConfig.debugMode) {
            ExtendedAE.LOGGER.debug("isInFilter check for item {}: isEmpty={}, isListed={}, final result={}",
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
        if (EAEConfig.debugMode && this.isInverted != inverted) {
            ExtendedAE.LOGGER.debug("Filter inversion changed to: {}", inverted);
        }
        this.isInverted = inverted;
    }

    @Override
    public boolean isInverted() {
        boolean result = !filter.isEmpty() && isInverted;
        if (EAEConfig.debugMode) {
            ExtendedAE.LOGGER.debug("isInverted check: !filter.isEmpty()={}, isInverted={}, result={}",
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
                
        if (EAEConfig.debugMode) {
            ExtendedAE.LOGGER.debug("canInsert check for item {}: {}", what, canInsert);
        }
        
        return canInsert;
    }

    @Override
    public void reduceOperationsRemaining(long inserted) {
        if (EAEConfig.debugMode) {
            ExtendedAE.LOGGER.debug("Reducing operations remaining by {}: {} -> {}",
                inserted, operationsRemaining, operationsRemaining - inserted);
        }
        operationsRemaining -= inserted;
    }
}
