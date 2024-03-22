package com.glodblock.github.extendedae.xmod.wt;

import appeng.api.config.Settings;
import appeng.api.config.ShowPatternProviders;
import appeng.api.util.IConfigManager;
import de.mari_023.ae2wtlib.terminal.ItemWT;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemUWirelessExPAT extends ItemWT {

    public ItemUWirelessExPAT() {
    }

    @Override
    public @NotNull MenuType<?> getMenuType(@NotNull ItemStack stack) {
        return ContainerUWirelessExPAT.TYPE;
    }

    @Override
    public @NotNull IConfigManager getConfigManager(@NotNull ItemStack target) {
        IConfigManager configManager = super.getConfigManager(target);
        configManager.registerSetting(Settings.TERMINAL_SHOW_PATTERN_PROVIDERS, ShowPatternProviders.VISIBLE);
        configManager.readFromNBT(target.getOrCreateTag().copy());
        return configManager;
    }
}
