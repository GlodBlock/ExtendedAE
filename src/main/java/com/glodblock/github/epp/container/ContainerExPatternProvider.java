package com.glodblock.github.epp.container;

import appeng.api.config.SecurityPermissions;
import appeng.helpers.iface.PatternProviderLogicHost;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.PatternProviderMenu;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerType;

public class ContainerExPatternProvider extends PatternProviderMenu {

    public static final ScreenHandlerType<ContainerExPatternProvider> TYPE = MenuTypeBuilder
            .create((id, inv, host) -> new ContainerExPatternProvider(id, inv, host), PatternProviderLogicHost.class)
            .requirePermission(SecurityPermissions.BUILD)
            .build("ex_pattern_provider");

    protected ContainerExPatternProvider(int id, PlayerInventory playerInventory, PatternProviderLogicHost host) {
        this(TYPE, id, playerInventory, host);
    }

    protected ContainerExPatternProvider(ScreenHandlerType<?> menuType, int id, PlayerInventory playerInventory, PatternProviderLogicHost host) {
        super(menuType, id, playerInventory, host);
    }


}
