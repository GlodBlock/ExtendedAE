package com.glodblock.github.appflux.util.helpers;

import appeng.api.upgrades.IUpgradeInventory;
import net.minecraft.world.level.ItemLike;

public interface IUpgradableMenu {

    IUpgradeInventory getUpgrades();

    boolean hasUpgrade(ItemLike upgradeCard);

}
