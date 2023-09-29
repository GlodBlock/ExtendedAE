package com.github.glodblock.eae.common;

import com.github.glodblock.eae.ExtendedAE;
import com.github.glodblock.eae.common.item.InfinityCell;
import com.github.glodblock.eae.register.ServerRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nonnull;

public class EAEItemAndBlock {

    public static final CreativeTabs TAB = new CreativeTabs(ExtendedAE.MODID) {
        @Override
        @Nonnull
        public ItemStack createIcon() {
            return new ItemStack(INFINITY_CELL);
        }
    };

    @GameRegistry.ObjectHolder(ExtendedAE.MODID + ":infinity_cell")
    public static InfinityCell INFINITY_CELL;

    public static void init(ServerRegister regHandler) {
        regHandler.item("infinity_cell", new InfinityCell());
    }

}
