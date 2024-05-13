package com.glodblock.github.extendedae.common.me.itemhost;

import appeng.api.networking.IGridNode;
import appeng.api.storage.IPatternAccessTermMenuHost;
import appeng.helpers.WirelessTerminalMenuHost;
import appeng.menu.ISubMenu;
import appeng.menu.locator.ItemMenuHostLocator;
import com.glodblock.github.extendedae.common.items.tools.ItemWirelessExPAT;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public class HostWirelessExPAT extends WirelessTerminalMenuHost<ItemWirelessExPAT> implements IPatternAccessTermMenuHost {

    public HostWirelessExPAT(ItemWirelessExPAT item, Player player, ItemMenuHostLocator locator, BiConsumer<Player, ISubMenu> returnToMainMenu) {
        super(item, player, locator, returnToMainMenu);
    }

    @Override
    public @Nullable IGridNode getGridNode() {
        return this.getActionableNode();
    }

}
