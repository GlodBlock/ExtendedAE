package com.glodblock.github.extendedae.container;

import appeng.menu.AEBaseMenu;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.slot.AppEngSlot;
import appeng.util.ConfigMenuInventory;
import com.glodblock.github.extendedae.api.CanerMode;
import com.glodblock.github.extendedae.client.ExSemantics;
import com.glodblock.github.extendedae.common.tileentities.TileCaner;
import com.glodblock.github.extendedae.network.EPPNetworkHandler;
import com.glodblock.github.glodium.network.packet.SGenericPacket;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import com.glodblock.github.glodium.network.packet.sync.Paras;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;

public class ContainerCaner extends AEBaseMenu implements IActionHolder {

    public static final MenuType<ContainerCaner> TYPE = MenuTypeBuilder
            .create(ContainerCaner::new, TileCaner.class)
            .build("caner");

    private final Map<String, Consumer<Paras>> actions = createHolder();
    @GuiSync(0)
    private CanerMode mode = CanerMode.FILL;
    private final TileCaner host;

    public ContainerCaner(int id, Inventory playerInventory, TileCaner host) {
        super(TYPE, id, playerInventory, host);
        this.host = host;
        this.actions.put("set", o -> this.setMode(o.get(0)));
        this.actions.put("update", o -> {
            if (this.getPlayer() instanceof ServerPlayer sp) {
                EPPNetworkHandler.INSTANCE.sendTo(new SGenericPacket("init", this.mode.ordinal()), sp);
            }
        });
        this.addSlot(new AppEngSlot(new ConfigMenuInventory(host.getStuff()), 0), ExSemantics.EX_1);
        this.addSlot(new AppEngSlot(host.getContainer(), 0), ExSemantics.EX_2);
        this.createPlayerInventorySlots(playerInventory);
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (this.mode != this.host.getMode()) {
            this.mode = this.host.getMode();
        }
    }

    public void setMode(int mode) {
        this.host.setMode(CanerMode.values()[mode]);
        this.broadcastChanges();
    }

    @NotNull
    @Override
    public Map<String, Consumer<Paras>> getActionMap() {
        return this.actions;
    }
}
