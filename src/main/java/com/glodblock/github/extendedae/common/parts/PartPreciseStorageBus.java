package com.glodblock.github.extendedae.common.parts;

import appeng.api.config.Actionable;
import appeng.api.config.Setting;
import appeng.api.ids.AEComponents;
import appeng.api.networking.security.IActionSource;
import appeng.api.parts.IPartItem;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import appeng.api.util.IConfigManager;
import appeng.helpers.IConfigInvHost;
import appeng.me.storage.NullInventory;
import appeng.util.ConfigInventory;
import appeng.util.SettingsFrom;
import appeng.util.prioritylist.IPartitionList;
import com.glodblock.github.extendedae.common.parts.base.PartSpecialStorageBus;
import com.glodblock.github.extendedae.container.ContainerPreciseStorageBus;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public class PartPreciseStorageBus extends PartSpecialStorageBus implements IConfigInvHost {

    private final ConfigInventory config = ConfigInventory.configStacks(63)
            .changeListener(this::onConfigurationChanged)
            .allowOverstacking(true)
            .build();

    public PartPreciseStorageBus(IPartItem<?> partItem) {
        super(partItem);
    }

    protected void onConfigurationChanged() {
        if (getMainNode().isReady()) {
            updateTarget(true);
        }
    }

    @Override
    protected int getUpgradeSlots() {
        return 5;
    }

    @Override
    public void onSettingChanged(IConfigManager manager, Setting<?> setting) {
        this.onConfigurationChanged();
        this.getHost().markForSave();
    }

    @Override
    public void upgradesChanged() {
        super.upgradesChanged();
        this.onConfigurationChanged();
    }

    @Override
    public void readFromNBT(ValueInput data) {
        super.readFromNBT(data);
        this.config.readFromChildTag(data, "config");
    }

    @Override
    public void writeToNBT(ValueOutput data) {
        super.writeToNBT(data);
        this.config.writeToChildTag(data, "config");
    }

    @Override
    public void importSettings(SettingsFrom mode, DataComponentMap input, @Nullable Player player) {
        super.importSettings(mode, input, player);
        var configInv = input.get(AEComponents.EXPORTED_CONFIG_INV);
        if (configInv != null) {
            this.config.readFromList(configInv);
        }
    }

    @Override
    public void exportSettings(SettingsFrom mode, DataComponentMap.Builder output) {
        super.exportSettings(mode, output);
        if (mode == SettingsFrom.MEMORY_CARD) {
            output.set(AEComponents.EXPORTED_CONFIG_INV, this.config.toList());
        }
    }

    @Override
    public ConfigInventory getConfig() {
        return this.config;
    }

    @Override
    protected IPartitionList createFilter() {
        return new PreciseFilter(this.config.getAvailableStacks());
    }

    @Override
    public MenuType<?> getMenuType() {
        return ContainerPreciseStorageBus.TYPE;
    }

    @Override
    protected StorageBusInventory createHandler() {
        return new PreciseInventory(NullInventory.of());
    }

    public MEStorage getInternalHandler() {
        return this.handler.getDelegate();
    }

    public static class PreciseInventory extends StorageBusInventory {

        public PreciseInventory(MEStorage inventory) {
            super(inventory);
        }

        @Override
        public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
            var filter = this.getPartitionList();
            long toAdd = 0;
            if (filter instanceof PreciseFilter p) {
                toAdd = p.getAmount(what);
            }
            if (filter.isEmpty()) {
                toAdd = 1;
            }
            if (toAdd <= 0) {
                return 0;
            }
            toAdd -= this.getAvailableStacks().get(what);
            if (toAdd <= 0) {
                return 0;
            }
            toAdd = Math.min(amount, toAdd);
            return super.insert(what, toAdd, mode, source);
        }

    }

    public record PreciseFilter(KeyCounter filter) implements IPartitionList {

        @Override
        public boolean isListed(AEKey input) {
            return this.filter.get(input) > 0;
        }

        @Override
        public boolean isEmpty() {
            return this.filter.isEmpty();
        }

        @Override
        public Iterable<AEKey> getItems() {
            return this.filter.keySet();
        }

        public long getAmount(AEKey input) {
            return this.filter.get(input);
        }

    }

}
