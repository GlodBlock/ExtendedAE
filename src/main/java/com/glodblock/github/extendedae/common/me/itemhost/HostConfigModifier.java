package com.glodblock.github.extendedae.common.me.itemhost;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.menu.locator.ItemMenuHostLocator;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.common.items.tools.ItemConfigModifier;
import net.minecraft.world.entity.player.Player;

public class HostConfigModifier extends ItemMenuHost<ItemConfigModifier> {

    private ItemConfigModifier.ConfigSettings settings;

    public HostConfigModifier(ItemConfigModifier item, Player player, ItemMenuHostLocator locator) {
        super(item, player, locator);
        this.settings = this.getItemStack().getOrDefault(EAESingletons.MODIFIER_CONFIG_SETTINGS, ItemConfigModifier.ConfigSettings.DEFAULT);
    }

    public ItemConfigModifier.ConfigSettings getSettings() {
        return this.settings;
    }

    public void setMode(ItemConfigModifier.ConfigSettings.Mode mode) {
        this.settings = new ItemConfigModifier.ConfigSettings(mode, this.settings.data());
        this.persist();
    }

    public void setData(long data) {
        this.settings = new ItemConfigModifier.ConfigSettings(this.settings.mode(), data);
        this.persist();
    }

    private void persist() {
        this.getItemStack().set(EAESingletons.MODIFIER_CONFIG_SETTINGS, this.settings);
    }

}
