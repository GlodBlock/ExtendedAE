package com.glodblock.github.extendedae.container;

import appeng.menu.AEBaseMenu;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.items.tools.ItemConfigModifier;
import com.glodblock.github.extendedae.common.me.itemhost.HostConfigModifier;
import com.glodblock.github.extendedae.network.EAENetworkHandler;
import com.glodblock.github.extendedae.network.packet.SEAEGenericPacket;
import com.glodblock.github.glodium.network.packet.sync.ActionMap;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

public class ContainerConfigModifier extends AEBaseMenu implements IActionHolder {

    private final ActionMap actions = ActionMap.create();
    public static final MenuType<@NotNull ContainerConfigModifier> TYPE = MenuTypeBuilder
            .create(ContainerConfigModifier::new, HostConfigModifier.class)
            .buildUnregistered(ExtendedAE.id("config_modifier"));

    private final HostConfigModifier host;
    @GuiSync(1)
    public ItemConfigModifier.ConfigSettings.Mode mode;
    @GuiSync(2)
    public long data;

    public ContainerConfigModifier(int id, Inventory playerInventory, HostConfigModifier host) {
        super(TYPE, id, playerInventory, host);
        this.host = host;
        this.actions.put("set_mode", o -> host.setMode(o.get(ItemConfigModifier.ConfigSettings.Mode.class)));
        this.actions.put("set_data", o -> host.setData(o.getLong()));
        this.actions.put("update", _ -> {
            if (this.getPlayer() instanceof ServerPlayer sp) {
                EAENetworkHandler.INSTANCE.sendTo(new SEAEGenericPacket("init", this.mode, this.data), sp);
            }
        });
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        this.mode = this.host.getSettings().mode();
        this.data = this.host.getSettings().data();
    }

    @Override
    public @NotNull ActionMap getActionMap() {
        return this.actions;
    }

}
