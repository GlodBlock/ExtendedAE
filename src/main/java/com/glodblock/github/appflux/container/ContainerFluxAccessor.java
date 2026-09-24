package com.glodblock.github.appflux.container;

import appeng.menu.AEBaseMenu;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import com.glodblock.github.appflux.AppFlux;
import com.glodblock.github.appflux.api.EnergyIO;
import com.glodblock.github.appflux.common.me.service.IEnergyDistributor;
import com.glodblock.github.glodium.network.packet.sync.ActionMap;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

public class ContainerFluxAccessor extends AEBaseMenu implements IActionHolder {

    public static final MenuType<ContainerFluxAccessor> TYPE = MenuTypeBuilder
            .create(ContainerFluxAccessor::new, IEnergyDistributor.class)
            .withInitialData(
                    (host, buffer) -> {
                        buffer.writeBoolean(host.isFastMode());
                        buffer.writeInt(host.getIOMode().ordinal());
                    }, (host, container, buffer) -> {
                        container.fastMode = buffer.readBoolean();
                        container.ioMode = buffer.readInt();
                    }
            )
            .buildUnregistered(AppFlux.id("flux_accessor"));

    private final ActionMap actions = ActionMap.create();
    private final IEnergyDistributor host;
    @GuiSync(1)
    public boolean fastMode;
    @GuiSync(2)
    public int ioMode;

    public ContainerFluxAccessor(int id, Inventory playerInventory, IEnergyDistributor host) {
        super(TYPE, id, playerInventory, host);
        this.host = host;
        this.actions.put("fast_mode", o -> this.setFastMode(o.get(0)));
        this.actions.put("io_mode", o -> this.setMode(o.get(0)));
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        this.fastMode = this.host.isFastMode();
        this.ioMode = this.host.getIOMode().ordinal();
    }

    public void setFastMode(boolean mode) {
        this.host.setFastMode(mode);
    }

    public void setMode(int mode) {
        if (mode < 0) {
            return;
        }
        this.host.setIOMode(EnergyIO.byId(mode));
    }

    @NotNull
    @Override
    public ActionMap getActionMap() {
        return this.actions;
    }

}
