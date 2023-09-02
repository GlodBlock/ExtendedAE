package com.github.glodblock.epp.container;

import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.PatternProviderMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class ContainerExPatternProvider extends PatternProviderMenu {

    public static final MenuType<ContainerExPatternProvider> TYPE = MenuTypeBuilder
            .create(ContainerExPatternProvider::new, PatternProviderLogicHost.class)
            .build("ex_pattern_provider");

    protected ContainerExPatternProvider(int id, Inventory playerInventory, PatternProviderLogicHost host) {
        super(TYPE, id, playerInventory, host);
    }

}
