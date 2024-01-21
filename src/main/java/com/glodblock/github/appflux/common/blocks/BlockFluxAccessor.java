package com.glodblock.github.appflux.common.blocks;

import appeng.block.AEBaseEntityBlock;
import com.glodblock.github.appflux.common.tileentities.TileFluxAccessor;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockFluxAccessor extends AEBaseEntityBlock<TileFluxAccessor> {

    public BlockFluxAccessor() {
        super(metalProps());
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable("block.appflux.flux_accessor.tooltip").withStyle(ChatFormatting.GRAY));
    }

}
