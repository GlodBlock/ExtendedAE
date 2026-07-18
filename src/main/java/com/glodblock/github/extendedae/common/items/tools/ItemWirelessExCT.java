package com.glodblock.github.extendedae.common.items.tools;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.core.AEConfig;
import appeng.items.tools.powered.WirelessTerminalItem;
import com.glodblock.github.extendedae.common.me.itemhost.HostWirelessExCT;
import com.glodblock.github.extendedae.container.ContainerWirelessExCT;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ItemWirelessExCT extends WirelessTerminalItem {

    public ItemWirelessExCT() {
        super(AEConfig.instance().getWirelessTerminalBattery(), new Properties().stacksTo(1));
    }

    @Override
    public MenuType<?> getMenuType() {
        return ContainerWirelessExCT.TYPE;
    }

    @Nullable
    @Override
    public ItemMenuHost getMenuHost(Player player, int inventorySlot, ItemStack stack, @Nullable BlockPos pos) {
        return new HostWirelessExCT(player, inventorySlot, stack, (p, sm) -> openFromInventory(p, inventorySlot, true));
    }

}
