package com.glodblock.github.extendedae.common.items.tools;

import appeng.api.config.Settings;
import appeng.api.config.ShowPatternProviders;
import appeng.api.util.IConfigManager;
import appeng.core.AEConfig;
import appeng.helpers.WirelessTerminalMenuHost;
import appeng.items.tools.powered.WirelessTerminalItem;
import appeng.menu.locator.ItemMenuHostLocator;
import appeng.util.ConfigManager;
import com.glodblock.github.extendedae.common.me.itemhost.HostWirelessExPAT;
import com.glodblock.github.extendedae.container.ContainerWirelessExPAT;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class ItemWirelessExPAT extends WirelessTerminalItem {

    public ItemWirelessExPAT() {
        super(AEConfig.instance().getWirelessTerminalBattery(), new Properties().stacksTo(1));
    }

    @Override
    public IConfigManager getConfigManager(ItemStack target) {
        var out = new ConfigManager((manager, settingName) -> manager.writeToNBT(target.getOrCreateTag()));
        out.registerSetting(Settings.TERMINAL_SHOW_PATTERN_PROVIDERS, ShowPatternProviders.VISIBLE);
        out.readFromNBT(target.getOrCreateTag().copy());
        return out;
    }

    @Override
    public MenuType<?> getMenuType() {
        return ContainerWirelessExPAT.TYPE;
    }

    @Override
    public @Nullable WirelessTerminalMenuHost<ItemWirelessExPAT> getMenuHost(Player player, ItemMenuHostLocator locator, @Nullable BlockHitResult hitResult) {
        return new HostWirelessExPAT(this, player, locator, (p, sm) -> openFromInventory(p, locator, true));
    }

}
