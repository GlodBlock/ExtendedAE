package com.github.glodblock.epp.container;

import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.PatternProviderMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class ContainerExPatternProvider extends PatternProviderMenu {

    public static final MenuType<ContainerExPatternProvider> TYPE = MenuTypeBuilder
            .create((id, inv, host) -> new ContainerExPatternProvider(id, inv, host), PatternProviderLogicHost.class)
            .build("ex_pattern_provider");

    protected ContainerExPatternProvider(int id, Inventory playerInventory, PatternProviderLogicHost host) {
        this(TYPE, id, playerInventory, host);
    }

    protected ContainerExPatternProvider(MenuType<?> menuType, int id, Inventory playerInventory, PatternProviderLogicHost host) {
        super(menuType, id, playerInventory, host);
    }

}
