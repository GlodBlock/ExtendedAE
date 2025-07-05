package com.glodblock.github.appflux.common.items;

import appeng.core.localization.Tooltips;
import appeng.items.AEBaseItem;
import com.glodblock.github.appflux.common.me.key.type.EnergyType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class ItemCreativeFECell extends AEBaseItem {

    public ItemCreativeFECell() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    @ParametersAreNonnullByDefault
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> lines, TooltipFlag advancedTooltips) {
        lines.add(Component.translatable("appflux.cell.storage", Tooltips.ofNumber(Integer.MAX_VALUE), EnergyType.FE.translate()));
    }

}
