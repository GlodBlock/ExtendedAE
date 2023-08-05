package com.github.glodblock.epp.container;

import appeng.api.util.IConfigurableObject;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.PatternAccessTermMenu;
import com.github.glodblock.epp.common.parts.PartExPatternAccessTerminal;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class ContainerExPatternTerminal extends PatternAccessTermMenu {

    public static final MenuType<ContainerExPatternTerminal> TYPE = MenuTypeBuilder
            .create(ContainerExPatternTerminal::new, PartExPatternAccessTerminal.class)
            .build("ex_pattern_access_terminal");

    public ContainerExPatternTerminal(int id, Inventory ip, IConfigurableObject host) {
        super(TYPE, id, ip, host, true);
    }
}
