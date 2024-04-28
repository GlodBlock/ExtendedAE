package com.glodblock.github.extendedae.container;

import appeng.core.definitions.AEItems;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.UpgradeableMenu;
import com.glodblock.github.extendedae.api.ThresholdMode;
import com.glodblock.github.extendedae.common.parts.PartThresholdExportBus;
import com.glodblock.github.extendedae.network.EPPNetworkHandler;
import com.glodblock.github.glodium.network.packet.SGenericPacket;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import com.glodblock.github.glodium.network.packet.sync.Paras;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;

public class ContainerThresholdExportBus extends UpgradeableMenu<PartThresholdExportBus> implements IActionHolder {

    public static final MenuType<ContainerThresholdExportBus> TYPE = MenuTypeBuilder
            .create(ContainerThresholdExportBus::new, PartThresholdExportBus.class)
            .build("threshold_export_bus");

    private final Map<String, Consumer<Paras>> actions = createHolder();
    @GuiSync(7)
    private ThresholdMode mode = ThresholdMode.GREATER;

    public ContainerThresholdExportBus(int id, Inventory ip, PartThresholdExportBus host) {
        super(TYPE, id, ip, host);
        this.actions.put("set", o -> this.setMode(o.get(0)));
        this.actions.put("update", o -> {
            if (this.getPlayer() instanceof ServerPlayer sp) {
                EPPNetworkHandler.INSTANCE.sendTo(new SGenericPacket("init", this.mode.ordinal()), sp);
            }
        });
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

    public boolean isConfigSlot(Slot slot) {
        return this.getSlots(SlotSemantics.CONFIG).contains(slot);
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (this.mode != this.getHost().getMode()) {
            this.mode = this.getHost().getMode();
        }
    }

    public void setMode(int mode) {
        this.getHost().setMode(ThresholdMode.values()[mode]);
        this.broadcastChanges();
    }

    public ThresholdMode getMode() {
        return this.mode;
    }

    @NotNull
    @Override
    public Map<String, Consumer<Paras>> getActionMap() {
        return this.actions;
    }

}