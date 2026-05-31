package com.glodblock.github.extendedae.common.parts;

import appeng.api.config.Settings;
import appeng.api.networking.IGrid;
import appeng.api.networking.storage.IStorageService;
import appeng.api.parts.IPartItem;
import appeng.api.stacks.GenericStack;
import appeng.parts.automation.ExportBusPart;
import appeng.parts.automation.StackWorldBehaviors;
import appeng.util.ConfigInventory;
import appeng.util.SettingsFrom;
import com.glodblock.github.extendedae.api.ThresholdMode;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.container.ContainerThresholdExportBus;
import com.glodblock.github.extendedae.util.Ae2Reflect;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PartThresholdExportBus extends ExportBusPart {

    private ConfigInventory config;
    private ThresholdMode mode = ThresholdMode.GREATER;

    public PartThresholdExportBus(IPartItem<?> partItem) {
        super(partItem);
    }

    public void setMode(ThresholdMode mode) {
        this.mode = mode;
    }

    public ThresholdMode getMode() {
        return this.mode;
    }

    @Override
    public void readFromNBT(ValueInput extra) {
        super.readFromNBT(extra);
        this.config.readFromChildTag(extra, "config2");
        this.mode = ThresholdMode.values()[extra.getByteOr("cmod", (byte) 0)];
    }

    @Override
    public void writeToNBT(ValueOutput extra) {
        super.writeToNBT(extra);
        this.config.writeToChildTag(extra, "config2");
        extra.putByte("cmod", (byte) this.mode.ordinal());
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

    @Override
    public void importSettings(SettingsFrom mode, DataComponentMap input, @Nullable Player player) {
        super.importSettings(mode, input, player);
        var tag = input.get(EAESingletons.EXTRA_SETTING);
        if (tag != null && tag.contains("threshold_mode")) {
            this.mode = ThresholdMode.values()[tag.getByteOr("threshold_mode", (byte) 0)];
        }
    }

    @Override
    public void exportSettings(SettingsFrom mode, DataComponentMap.Builder output) {
        super.exportSettings(mode, output);
        if (mode == SettingsFrom.MEMORY_CARD) {
            var tag = new CompoundTag();
            tag.putByte("threshold_mode", (byte) this.mode.ordinal());
            output.set(EAESingletons.EXTRA_SETTING, tag);
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    protected boolean doBusWork(IGrid grid) {
        var storageService = grid.getStorageService();
        var schedulingMode = this.getConfigManager().getSetting(Settings.SCHEDULING_MODE);

        var context = Ae2Reflect.getExportContext(this, storageService, grid.getEnergyService());

        int x;
        for (x = 0; x < this.availableSlots() && context.hasOperationsLeft(); x++) {
            final int slotToExport = this.getStartingSlot(schedulingMode, x);
            var stack = getConfig().getStack(slotToExport);
            if (stack == null || !checkAmount(stack, storageService)) {
                continue;
            }
            var what = stack.what();
            var transferFactor = what.getAmountPerOperation();
            long amount = Math.min((long) context.getOperationsRemaining() * transferFactor, this.getMaxOutput(stack, storageService));
            amount = getExportStrategy().transfer(context, what, amount);
            if (amount > 0) {
                context.reduceOperationsRemaining(Math.max(1, amount / transferFactor));
            }
        }

        // Round-robin should only advance if something was actually exported
        if (context.hasDoneWork()) {
            this.updateSchedulingMode(schedulingMode, x);
        }

        return context.hasDoneWork();
    }

    private boolean checkAmount(@NotNull GenericStack stack, @NotNull IStorageService service) {
        long thr = stack.amount();
        long stored = service.getCachedInventory().get(stack.what());
        if (this.mode == ThresholdMode.GREATER) {
            return stored > thr;
        } else if (this.mode == ThresholdMode.LOWER) {
            return stored <= thr;
        } else {
            return false;
        }
    }

    // Don't output too much
    private long getMaxOutput(@NotNull GenericStack stack, @NotNull IStorageService service) {
        if (this.mode == ThresholdMode.GREATER) {
            return service.getCachedInventory().get(stack.what()) - stack.amount();
        } else {
            return Long.MAX_VALUE;
        }
    }

    @Override
    protected MenuType<?> getMenuType() {
        return ContainerThresholdExportBus.TYPE;
    }

}
