package com.github.glodblock.extendedae.common.me.itemhost;

import appeng.helpers.WirelessTerminalMenuHost;
import appeng.menu.ISubMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public class HostWirelessExPAT extends WirelessTerminalMenuHost {

    public HostWirelessExPAT(Player player, @Nullable Integer slot, ItemStack itemStack, BiConsumer<Player, ISubMenu> returnToMainMenu) {
        super(player, slot, itemStack, returnToMainMenu);
    }

}
