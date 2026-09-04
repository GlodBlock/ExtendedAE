package com.glodblock.github.extendedae.container;

import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.UpgradeableMenu;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.parts.PartModExportBus;
import com.glodblock.github.glodium.network.packet.sync.ActionMap;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

public class ContainerModExportBus extends UpgradeableMenu<PartModExportBus> implements IActionHolder {
    private final ActionMap actions = ActionMap.create();

    public static final MenuType<ContainerModExportBus> TYPE = MenuTypeBuilder
            .create(ContainerModExportBus::new, PartModExportBus.class)
            .withInitialData(
                    (host, buf) -> buf.writeUtf(host.getModNameFilter()),
                    (host, container, buf) -> container.exp = buf.readUtf()
            )
            .buildUnregistered(ExtendedAE.id("mod_export_bus"));

    @GuiSync(9)
    public String exp;

    public ContainerModExportBus(int id, Inventory ip, PartModExportBus host) {
        super(TYPE, id, ip, host);
        this.actions.put("set", o -> this.setExp(o.get(0)));
        this.exp = host.getModNameFilter();
    }

    @Override
    protected void setupConfig() {
        // NO-OP
    }

    @Override
    public boolean isSlotEnabled(int idx) {
        return false;
    }

    @Override
    public void removed(@NotNull Player player) {
        this.getHost().setModNameFilter(this.exp);
        super.removed(player);
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    @NotNull
    @Override
    public ActionMap getActionMap() {
        return this.actions;
    }


}
