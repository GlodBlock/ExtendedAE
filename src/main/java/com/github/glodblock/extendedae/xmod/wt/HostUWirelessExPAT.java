package com.github.glodblock.extendedae.xmod.wt;

import appeng.menu.ISubMenu;
import com.github.glodblock.extendedae.common.EAEItemAndBlock;
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

    @Override
    public ItemStack getMainMenuIcon() {
        return new ItemStack(EAEItemAndBlock.WIRELESS_EX_PAT);
    }
}
