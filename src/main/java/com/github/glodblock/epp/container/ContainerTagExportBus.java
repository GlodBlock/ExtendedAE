package com.github.glodblock.epp.container;

import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.UpgradeableMenu;
import com.github.glodblock.epp.common.parts.PartTagExportBus;
import com.github.glodblock.epp.network.EPPNetworkHandler;
import com.glodblock.github.glodium.network.packet.SGenericPacket;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import com.glodblock.github.glodium.network.packet.sync.Paras;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;

public class ContainerTagExportBus extends UpgradeableMenu<PartTagExportBus> implements IActionHolder {
    private final Map<String, Consumer<Paras>> actions = createHolder();

    public static final MenuType<ContainerTagExportBus> TYPE = MenuTypeBuilder
            .create(ContainerTagExportBus::new, PartTagExportBus.class)
            .build("tag_export_bus");

    @GuiSync(9)
    public String exp = "";

    public ContainerTagExportBus(int id, Inventory ip, PartTagExportBus host) {
        super(TYPE, id, ip, host);
        this.actions.put("set", o -> this.setExp(o.get(0)));
        this.actions.put("update", o -> {
            if (this.getPlayer() instanceof ServerPlayer sp) {
                EPPNetworkHandler.INSTANCE.sendTo(new SGenericPacket("init", this.exp), sp);
            }
        });
    }

    @Override
    protected void setupConfig() {
        // NO-OP
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (!this.exp.equals(getHost().getTagFilter())) {
            this.exp = getHost().getTagFilter();
        }
    }

    @Override
    public boolean isSlotEnabled(int idx) {
        return false;
    }

    public void setExp(String exp) {
        getHost().setTagFilter(exp);
        this.broadcastChanges();
    }

    @NotNull
    @Override
    public Map<String, Consumer<Paras>> getActionMap() {
        return this.actions;
    }


}
