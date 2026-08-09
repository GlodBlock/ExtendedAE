package com.glodblock.github.extendedae.container;

import appeng.core.definitions.AEItems;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.UpgradeableMenu;
import com.glodblock.github.extendedae.api.BatchMode;
import com.glodblock.github.extendedae.common.parts.PartPreciseExportBus;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import com.glodblock.github.glodium.network.packet.sync.Paras;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;

public class ContainerPreciseExportBus extends UpgradeableMenu<PartPreciseExportBus> implements IActionHolder {

    public static final MenuType<ContainerPreciseExportBus> TYPE = MenuTypeBuilder
            .create(ContainerPreciseExportBus::new, PartPreciseExportBus.class)
            .withInitialData(
                    (host, buffer) -> buffer.writeEnum(host.getMode()),
                    (host, menu, buffer) -> menu.mode = buffer.readEnum(BatchMode.class)
            )
            .build("precise_export_bus");

    private final Map<String, Consumer<Paras>> actions = createHolder();
    @GuiSync(2)
    private BatchMode mode = BatchMode.EXACT;

    public ContainerPreciseExportBus(int id, Inventory ip, PartPreciseExportBus host) {
        super(TYPE, id, ip, host);
        this.actions.put("set", o -> this.setMode(o.get(0)));
    }

    @Override
    protected void setupConfig() {
        addExpandableConfigSlots(getHost().getConfig(), 2, 9, 5);
    }

    @Override
    public boolean isSlotEnabled(int idx) {
        final int upgrades = getUpgrades().getInstalledUpgrades(AEItems.CAPACITY_CARD);
        return upgrades > idx;
    }

    public BatchMode getMode() {
        return this.mode;
    }

    @Override
    public void broadcastChanges() {
        this.mode = this.getHost().getMode();
        super.broadcastChanges();
    }

    public void setMode(int mode) {
        this.getHost().setMode(BatchMode.values()[mode]);
    }

    public boolean isConfigSlot(Slot slot) {
        return this.getSlots(SlotSemantics.CONFIG).contains(slot);
    }

    @NotNull
    @Override
    public Map<String, Consumer<Paras>> getActionMap() {
        return this.actions;
    }

}