package com.glodblock.github.appflux.container;

import appeng.menu.AEBaseMenu;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import com.glodblock.github.appflux.AppFlux;
import com.glodblock.github.appflux.common.me.service.IEnergyDistributor;
import com.glodblock.github.appflux.network.AFNetworkHandler;
import com.glodblock.github.appflux.network.SAFGenericPacket;
import com.glodblock.github.glodium.network.packet.sync.ActionMap;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

public class ContainerFluxAccessor extends AEBaseMenu implements IActionHolder {

    public static final MenuType<@NotNull ContainerFluxAccessor> TYPE = MenuTypeBuilder
            .create(ContainerFluxAccessor::new, IEnergyDistributor.class)
            .buildUnregistered(AppFlux.id("flux_accessor"));

    private final ActionMap actions = ActionMap.create();
    private final IEnergyDistributor host;
    @GuiSync(1)
    public boolean fastMode;

    public ContainerFluxAccessor(int id, Inventory playerInventory, IEnergyDistributor host) {
        super(TYPE, id, playerInventory, host);
        this.host = host;
        this.actions.put("set", o -> this.setMode(o.getBoolean()));
        this.actions.put("update", _ -> {
            if (this.getPlayer() instanceof ServerPlayer sp) {
                AFNetworkHandler.INSTANCE.sendTo(new SAFGenericPacket("init", this.fastMode), sp);
            }
        });
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        this.fastMode = this.host.isFastMode();
    }

    public void setMode(boolean mode) {
        this.host.setFastMode(mode);
        this.broadcastChanges();
    }

    @NotNull
    @Override
    public ActionMap getActionMap() {
        return this.actions;
    }

}
