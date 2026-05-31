package com.glodblock.github.extendedae.common.items;

import appeng.core.localization.GuiText;
import appeng.core.localization.Tooltips;
import appeng.items.parts.PartItem;
import com.glodblock.github.extendedae.common.parts.PartSmartAnnihilationPlane;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.Enchantable;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ItemSmartAnnihilationPlane extends PartItem<PartSmartAnnihilationPlane> {

    public ItemSmartAnnihilationPlane(Properties properties) {
        super(properties.component(DataComponents.ENCHANTABLE, new Enchantable(10)).component(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY), PartSmartAnnihilationPlane.class, PartSmartAnnihilationPlane::new);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull TooltipDisplay tooltipDisplay, @NotNull Consumer<Component> lines, @NotNull TooltipFlag tooltipFlags) {
        super.appendHoverText(stack, context, tooltipDisplay, lines, tooltipFlags);
        var enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        if (enchantments.isEmpty()) {
            lines.accept(Tooltips.of(GuiText.CanBeEnchanted));
        } else {
            lines.accept(Tooltips.of(GuiText.IncreasedEnergyUseFromEnchants));
        }
    }

    @Override
    public void addToMainCreativeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        super.addToMainCreativeTab(parameters, output);
        var enchantmentRegistry = parameters.holders().lookupOrThrow(Registries.ENCHANTMENT);
        var enchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enchantments.set(enchantmentRegistry.getOrThrow(Enchantments.SILK_TOUCH), 1);
        var silkTouch = new ItemStack(this);
        silkTouch.set(DataComponents.ENCHANTMENTS, enchantments.toImmutable());
        output.accept(silkTouch);
    }

}
