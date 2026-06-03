package com.github.glodblock.eae.common;

import com.github.glodblock.eae.EAETags;
import com.github.glodblock.eae.common.item.InfinityCell;
import com.github.glodblock.eae.register.ServerRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.jetbrains.annotations.NotNull;

public class EAEItemAndBlock {

    public static final CreativeTabs TAB = new CreativeTabs(EAETags.MOD_ID) {
        @Override
        public @NotNull ItemStack createIcon() {
            return new ItemStack(INFINITY_CELL);
        }
    };

    @GameRegistry.ObjectHolder(EAETags.MOD_ID + ":infinity_cell")
    public static InfinityCell INFINITY_CELL;

    public static void init(ServerRegister regHandler) {
        regHandler.item("infinity_cell", new InfinityCell());
    }
}
