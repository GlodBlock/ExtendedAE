package com.glodblock.github.extendedae.common.items.tools;

import appeng.api.config.Settings;
import appeng.api.config.ShowPatternProviders;
import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.api.util.IConfigManager;
import appeng.core.AEConfig;
import appeng.items.tools.powered.WirelessTerminalItem;
import appeng.util.ConfigManager;
import com.glodblock.github.extendedae.common.me.itemhost.HostWirelessExPAT;
import com.glodblock.github.extendedae.container.ContainerWirelessExPAT;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ItemWirelessExPAT extends WirelessTerminalItem {

    public ItemWirelessExPAT() {
        super(AEConfig.instance().getWirelessTerminalBattery(), new Properties().stacksTo(1));
    }

    @Override
    public IConfigManager getConfigManager(ItemStack target) {
        var out = new ConfigManager((manager, settingName) -> {
            manager.writeToNBT(target.getOrCreateTag());
        });
        out.registerSetting(Settings.TERMINAL_SHOW_PATTERN_PROVIDERS, ShowPatternProviders.VISIBLE);
        out.readFromNBT(target.getOrCreateTag().copy());
        return out;
    }

    @Override
    public MenuType<?> getMenuType() {
        return ContainerWirelessExPAT.TYPE;
    }

    @Nullable
    @Override
    public ItemMenuHost getMenuHost(Player player, int inventorySlot, ItemStack stack, @Nullable BlockPos pos) {
        return new HostWirelessExPAT(player, inventorySlot, stack, (p, sm) -> openFromInventory(p, inventorySlot, true));
    }

}
