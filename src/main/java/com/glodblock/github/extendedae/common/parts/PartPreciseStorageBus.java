package com.glodblock.github.extendedae.common.parts;

import appeng.api.config.Actionable;
import appeng.api.config.Setting;
import appeng.api.ids.AEComponents;
import appeng.api.networking.security.IActionSource;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import appeng.api.util.IConfigManager;
import appeng.core.AppEng;
import appeng.helpers.IConfigInvHost;
import appeng.me.storage.NullInventory;
import appeng.parts.PartModel;
import appeng.util.ConfigInventory;
import appeng.util.SettingsFrom;
import appeng.util.prioritylist.IPartitionList;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.parts.base.PartSpecialStorageBus;
import com.glodblock.github.extendedae.container.ContainerPreciseStorageBus;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public class PartPreciseStorageBus extends PartSpecialStorageBus implements IConfigInvHost {

    public static final ResourceLocation MODEL_BASE = ResourceLocation.fromNamespaceAndPath(ExtendedAE.MODID, "part/precise_storage_bus_base");
    public static final IPartModel MODELS_OFF = new PartModel(MODEL_BASE, ResourceLocation.fromNamespaceAndPath(AppEng.MOD_ID, "part/storage_bus_off"));
    public static final IPartModel MODELS_ON = new PartModel(MODEL_BASE, ResourceLocation.fromNamespaceAndPath(AppEng.MOD_ID, "part/storage_bus_on"));
    public static final IPartModel MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE, ResourceLocation.fromNamespaceAndPath(AppEng.MOD_ID, "part/storage_bus_has_channel"));
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
    public void readFromNBT(CompoundTag data, HolderLookup.Provider registries) {
        super.readFromNBT(data, registries);
        this.config.readFromChildTag(data, "config", registries);
    }

    @Override
    public void writeToNBT(CompoundTag data, HolderLookup.Provider registries) {
        super.writeToNBT(data, registries);
        this.config.writeToChildTag(data, "config", registries);
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

    public static class PreciseFilter implements IPartitionList {

        private final KeyCounter filter;

        public PreciseFilter(KeyCounter counter) {
            this.filter = counter;
        }

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
