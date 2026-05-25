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
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemSmartAnnihilationPlane extends PartItem<PartSmartAnnihilationPlane> {

    public static final ThreadLocal<Object> CALLING_DAMAGEABLE_FROM_ANVIL = ThreadLocal.withInitial(() -> null);

    public ItemSmartAnnihilationPlane() {
        super(new Properties(), PartSmartAnnihilationPlane.class, PartSmartAnnihilationPlane::new);
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getEnchantmentValue() {
        return 10;
    }

    @Override
    public int getMaxDamage(@NotNull ItemStack stack) {
        return CALLING_DAMAGEABLE_FROM_ANVIL.get() != null ? 1 : super.getMaxDamage(stack);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> lines, @NotNull TooltipFlag isAdvanced) {
        super.appendHoverText(stack, context, lines, isAdvanced);
        var enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        if (enchantments.isEmpty()) {
            lines.add(Tooltips.of(GuiText.CanBeEnchanted));
        } else {
            lines.add(Tooltips.of(GuiText.IncreasedEnergyUseFromEnchants));
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
