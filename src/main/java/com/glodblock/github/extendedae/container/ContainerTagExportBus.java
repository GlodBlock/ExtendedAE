package com.glodblock.github.extendedae.container;

import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.UpgradeableMenu;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.parts.PartTagExportBus;
import com.glodblock.github.glodium.network.packet.sync.ActionMap;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

public class ContainerTagExportBus extends UpgradeableMenu<PartTagExportBus> implements IActionHolder {

    private final ActionMap actions = ActionMap.create();

    public static final MenuType<@NotNull ContainerTagExportBus> TYPE = MenuTypeBuilder
            .create(ContainerTagExportBus::new, PartTagExportBus.class)
            .withInitialData((host, buf) -> {
                buf.writeUtf(host.getTagFilter(true));
                buf.writeUtf(host.getTagFilter(false));
            }, (_, container, buf) -> {
                container.exp = buf.readUtf();
                container.exp2 = buf.readUtf();
            })
            .buildUnregistered(ExtendedAE.id("tag_export_bus"));

    public String exp;
    public String exp2;

    public ContainerTagExportBus(int id, Inventory ip, PartTagExportBus host) {
        super(TYPE, id, ip, host);
        this.exp = host.getTagFilter(true);
        this.exp2 = host.getTagFilter(false);
        this.actions.put("set", o -> this.setExp(o.getString(), o.getBoolean()));
    }

    @Override
    protected void setupConfig() {
        // NO-OP
    }

    @Override
    public boolean isSlotEnabled(int idx) {
        return false;
    }

    public void setExp(String exp, boolean isWhite) {
        if (isWhite) {
            this.exp = exp;
        } else {
            this.exp2 = exp;
        }
    }

    @Override
    public void removed(@NotNull Player player) {
        this.getHost().setTagFilter(this.exp, true);
        this.getHost().setTagFilter(this.exp2, false);
        super.removed(player);
    }

    @NotNull
    @Override
    public ActionMap getActionMap() {
        return this.actions;
    }

}
