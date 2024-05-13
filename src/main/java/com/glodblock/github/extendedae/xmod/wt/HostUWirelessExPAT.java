package com.glodblock.github.extendedae.xmod.wt;

import appeng.api.networking.IGridNode;
import appeng.api.storage.IPatternAccessTermMenuHost;
import appeng.menu.ISubMenu;
import appeng.menu.locator.ItemMenuHostLocator;
import de.mari_023.ae2wtlib.terminal.ItemWT;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public class HostUWirelessExPAT extends WTMenuHost implements IPatternAccessTermMenuHost {

    public HostUWirelessExPAT(ItemWT item, Player player, ItemMenuHostLocator locator, BiConsumer<Player, ISubMenu> returnToMainMenu) {
        super(item, player, locator, returnToMainMenu);
    }

    public @Nullable IGridNode getGridNode() {
        return this.getActionableNode();
    }

}
