package com.github.glodblock.extendedae.container;

import appeng.menu.AEBaseMenu;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.slot.AppEngSlot;
import appeng.util.ConfigMenuInventory;
import com.github.glodblock.extendedae.api.CanerMode;
import com.github.glodblock.extendedae.client.ExSemantics;
import com.github.glodblock.extendedae.common.tileentities.TileCaner;
import com.github.glodblock.extendedae.network.EAENetworkServer;
import com.github.glodblock.extendedae.network.packet.SGenericPacket;
import com.github.glodblock.extendedae.network.packet.sync.IActionHolder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
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

    private final Map<String, Consumer<Object[]>> actions = new Object2ObjectOpenHashMap<>();
    @GuiSync(0)
    private CanerMode mode = CanerMode.FILL;
    private final TileCaner host;

    public ContainerCaner(int id, Inventory playerInventory, TileCaner host) {
        super(TYPE, id, playerInventory, host);
        this.host = host;
        this.actions.put("set", o -> this.setMode((Integer) o[0]));
        this.actions.put("update", o -> {
            if (this.getPlayer() instanceof ServerPlayer sp) {
                EAENetworkServer.INSTANCE.sendTo(new SGenericPacket("init", this.mode.ordinal()), sp);
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
    public Map<String, Consumer<Object[]>> getActionMap() {
        return this.actions;
    }
}
