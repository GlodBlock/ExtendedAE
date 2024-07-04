package com.glodblock.github.extendedae.common.parts;

import appeng.api.behaviors.StackTransferContext;
import appeng.api.config.Actionable;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.networking.IGrid;
import appeng.api.networking.crafting.ICraftingService;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.api.stacks.AEKey;
import appeng.api.storage.StorageHelper;
import appeng.core.AppEngBase;
import appeng.core.definitions.AEItems;
import appeng.parts.PartModel;
import appeng.parts.automation.ExportBusPart;
import appeng.parts.automation.StackWorldBehaviors;
import appeng.util.ConfigInventory;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.container.ContainerPreciseExportBus;
import com.glodblock.github.extendedae.util.Ae2Reflect;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;

import java.util.Arrays;
import java.util.List;

public class PartPreciseExportBus extends ExportBusPart {

    public static List<ResourceLocation> MODELS = Arrays.asList(
            ResourceLocation.fromNamespaceAndPath(ExtendedAE.MODID, "part/precise_export_bus_base"),
            ResourceLocation.fromNamespaceAndPath(AppEngBase.MOD_ID, "part/export_bus_on"),
            ResourceLocation.fromNamespaceAndPath(AppEngBase.MOD_ID, "part/export_bus_off"),
            ResourceLocation.fromNamespaceAndPath(AppEngBase.MOD_ID, "part/export_bus_has_channel")
    );

    public static final PartModel MODELS_OFF = new PartModel(MODELS.get(0), MODELS.get(2));
    public static final PartModel MODELS_ON = new PartModel(MODELS.get(0), MODELS.get(1));
    public static final PartModel MODELS_HAS_CHANNEL = new PartModel(MODELS.get(0), MODELS.get(3));
    private ConfigInventory config;

    public PartPreciseExportBus(IPartItem<?> partItem) {
        super(partItem);
    }

    @Override
    public void readFromNBT(CompoundTag extra, HolderLookup.Provider registries) {
        super.readFromNBT(extra, registries);
        this.config.readFromChildTag(extra, "config2", registries);
    }

    @Override
    public void writeToNBT(CompoundTag extra, HolderLookup.Provider registries) {
        super.writeToNBT(extra, registries);
        this.config.writeToChildTag(extra, "config2", registries);
    }

    @Override
    public ConfigInventory getConfig() {
        if (this.config == null) {
            this.config = ConfigInventory.configStacks(63)
                    .supportedTypes(StackWorldBehaviors.withExportStrategy())
                    .changeListener(() -> Ae2Reflect.updatePartState(this))
                    .allowOverstacking(true)
                    .build();
        }
        return this.config;
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

            var before = context.getOperationsRemaining();
            if (before < Math.max(1, amount / transferFactor)) {
                break;
            }

            var toSend = getExportStrategy().push(what, amount, Actionable.SIMULATE);
            if (toSend == amount) {
                var canExt = simulateExtract(context, what, amount);
                if (canExt == amount) {
                    var realSend = getExportStrategy().transfer(context, what, amount);
                    if (realSend > 0) {
                        context.reduceOperationsRemaining(Math.max(1, amount / transferFactor));
                    }
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

    @Override
    protected int getOperationsPerTick() {
        // 64 is the max stack player theoretically can put in a slot.
        return 64;
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

}
