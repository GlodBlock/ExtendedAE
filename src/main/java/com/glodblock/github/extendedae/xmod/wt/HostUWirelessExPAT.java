package com.glodblock.github.extendedae.xmod.wt;

import appeng.menu.ISubMenu;
import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public class HostUWirelessExPAT extends WTMenuHost {
    public HostUWirelessExPAT(Player ep, @Nullable Integer inventorySlot, ItemStack is, BiConsumer<Player, ISubMenu> returnToMainMenu) {
        super(ep, inventorySlot, is, returnToMainMenu);
        this.readFromNbt();
    }

    public ItemStack getMainMenuIcon() {
        return new ItemStack(EPPItemAndBlock.WIRELESS_EX_PAT);
    }
}
