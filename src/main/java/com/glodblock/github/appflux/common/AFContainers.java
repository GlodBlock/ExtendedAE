package com.glodblock.github.appflux.common;

import appeng.api.implementations.menuobjects.IPortableTerminal;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.me.common.MEStorageMenu;
import net.minecraft.world.inventory.MenuType;

public class AFContainers {

    public static final MenuType<MEStorageMenu> PORTABLE_FE_CELL_TYPE = MenuTypeBuilder
            .create(MEStorageMenu::new, IPortableTerminal.class)
            .build("portable_fe_cell");

}
