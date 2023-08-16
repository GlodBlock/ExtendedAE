package com.glodblock.github.epp.common.items;

import appeng.blockentity.crafting.PatternProviderBlockEntity;
import appeng.parts.crafting.PatternProviderPart;
import com.glodblock.github.epp.common.EPPItemAndBlock;
import com.glodblock.github.epp.common.tiles.TileExPatternProvider;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemPatternProviderUpgrade extends ItemUpgrade {

    public ItemPatternProviderUpgrade() {
        super(new Item.Settings().group(EPPItemAndBlock.TAB));
        this.addTile(PatternProviderBlockEntity.class, EPPItemAndBlock.EX_PATTERN_PROVIDER, TileExPatternProvider.TYPE);
        this.addPart(PatternProviderPart.class, EPPItemAndBlock.EX_PATTERN_PROVIDER_PART);
    }

    @Override
    public void appendTooltip(@NotNull ItemStack stack, @Nullable World level, @NotNull List<Text> list, @NotNull TooltipContext tooltipFlag) {
        list.add(Text.translatable("epp.upgrade.tooltip.01").formatted(Formatting.GRAY));
        list.add(Text.translatable("epp.upgrade.tooltip.02").formatted(Formatting.GRAY));
        super.appendTooltip(stack, level, list, tooltipFlag);
    }

}
