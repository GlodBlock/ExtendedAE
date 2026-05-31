package com.glodblock.github.extendedae.common.parts;

import appeng.api.behaviors.StackTransferContext;
import appeng.api.config.Actionable;
import appeng.api.config.RedstoneMode;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.networking.IGrid;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.networking.crafting.ICraftingService;
import appeng.api.parts.IPartItem;
import appeng.api.stacks.AEKey;
import appeng.api.storage.StorageHelper;
import appeng.core.definitions.AEItems;
import appeng.parts.automation.ExportBusPart;
import appeng.parts.automation.StackWorldBehaviors;
import appeng.util.ConfigInventory;
import com.glodblock.github.extendedae.config.EAEConfig;
import com.glodblock.github.extendedae.container.ContainerPreciseExportBus;
import com.glodblock.github.extendedae.util.Ae2Reflect;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class PartPreciseExportBus extends ExportBusPart {

    private ConfigInventory config;
    private Object2LongMap<AEKey> amountMap;

    public PartPreciseExportBus(IPartItem<?> partItem) {
        super(partItem);
    }

    @Override
    public void readFromNBT(ValueInput extra) {
        super.readFromNBT(extra);
        this.config.readFromChildTag(extra, "config2");
        this.rebuildAmountMap();
    }

    private void rebuildAmountMap() {
        this.amountMap = new Object2LongOpenHashMap<>();
        for (int x = 0; x < this.config.size(); x ++) {
            var stack = this.config.getStack(x);
            if (stack != null && stack.amount() > 0) {
                this.amountMap.put(stack.what(), stack.amount());
            }
        }
    }

    @Override
    public void writeToNBT(ValueOutput extra) {
        super.writeToNBT(extra);
        this.config.writeToChildTag(extra, "config2");
    }

    @Override
    public ConfigInventory getConfig() {
        if (this.config == null) {
            this.config = new PreciseInventory(this);
        }
        return this.config;
    }

    private void onConfigChange() {
        Ae2Reflect.updatePartState(this);
        this.amountMap = null;
    }

    private boolean craftOnly() {
        return isCraftingEnabled() && this.getConfigManager().getSetting(Settings.CRAFT_ONLY) == YesNo.YES;
    }

    private boolean isCraftingEnabled() {
        return isUpgradedWith(AEItems.CRAFTING_CARD);
    }

    @SuppressWarnings("UnstableApiUsage")
    private void attemptCrafting(StackTransferContext context, ICraftingService cg, int slotToExport, AEKey what, long targetAmount) {
        var amount = getExportStrategy().push(what, targetAmount, Actionable.SIMULATE);
        if (amount == targetAmount) {
            requestCrafting(cg, slotToExport, what, amount);
            context.reduceOperationsRemaining(Math.max(1, amount / what.getAmountPerOperation()));
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public long insertCraftedItems(ICraftingLink link, AEKey what, long amount, Actionable mode) {
        if (this.amountMap == null) {
            this.rebuildAmountMap();
        }
        var amt = this.amountMap.getLong(what);
        if (amt > 0 && amount >= amt) {
            var added = getExportStrategy().push(what, amt, Actionable.SIMULATE);
            if (added == amt) {
                return super.insertCraftedItems(link, what, amt, mode);
            }
        }
        return 0;
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    protected boolean doBusWork(IGrid grid) {
        var storageService = grid.getStorageService();
        var cg = grid.getCraftingService();
        var schedulingMode = this.getConfigManager().getSetting(Settings.SCHEDULING_MODE);

        var context = Ae2Reflect.getExportContext(this, storageService, grid.getEnergyService());

        int x;
        for (x = 0; x < this.availableSlots() && context.hasOperationsLeft(); x++) {
            final int slotToExport = this.getStartingSlot(schedulingMode, x);
            var stack = getConfig().getStack(slotToExport);

            if (stack == null) {
                continue;
            }
            var what = stack.what();
            var amount = stack.amount();
            var transferFactor = what.getAmountPerOperation();

            if (this.craftOnly()) {
                this.attemptCrafting(context, cg, slotToExport, what, amount);
                continue;
            }

            long before = context.getOperationsRemaining();
            if (before < Math.max(1, amount / transferFactor)) {
                break;
            }

            long ceil = this.checkPulseRS() ? simulateExtract(context, what, amount) : (simulateExtract(context, what, before * transferFactor) / amount * amount);
            long canHold = getExportStrategy().push(what, ceil, Actionable.SIMULATE) / amount * amount;
            if (canHold > 0) {
                var realSend = getExportStrategy().transfer(context, what, canHold);
                if (realSend > 0) {
                    context.reduceOperationsRemaining(Math.max(1, canHold / transferFactor));
                }
            }

            if (before == context.getOperationsRemaining() && this.isCraftingEnabled()) {
                this.attemptCrafting(context, cg, slotToExport, what, amount);
            }
        }

        // Round-robin should only advance if something was actually exported
        if (context.hasDoneWork()) {
            this.updateSchedulingMode(schedulingMode, x);
        }

        return context.hasDoneWork();
    }

    private boolean checkPulseRS() {
        if (this.getUpgrades().isInstalled(AEItems.REDSTONE_CARD)) {
            return this.getRSMode() == RedstoneMode.SIGNAL_PULSE;
        }
        return false;
    }

    @Override
    protected int getOperationsPerTick() {
        return super.getOperationsPerTick() * EAEConfig.busSpeed;
    }

    @Override
    protected MenuType<?> getMenuType() {
        return ContainerPreciseExportBus.TYPE;
    }

    @SuppressWarnings("UnstableApiUsage")
    private static long simulateExtract(StackTransferContext context, AEKey what, long amount) {
        var inv = context.getInternalStorage();
        return StorageHelper.poweredExtraction(
                context.getEnergySource(),
                inv.getInventory(),
                what,
                amount,
                context.getActionSource(),
                Actionable.SIMULATE);
    }

    static class PreciseInventory extends ConfigInventory {

        protected PreciseInventory(PartPreciseExportBus host) {
            super(StackWorldBehaviors.withExportStrategy(), null, Mode.CONFIG_STACKS, 63, host::onConfigChange, true);
        }

        @Override
        public long getMaxAmount(AEKey key) {
            return 64L * key.getAmountPerUnit();
        }

    }

}
