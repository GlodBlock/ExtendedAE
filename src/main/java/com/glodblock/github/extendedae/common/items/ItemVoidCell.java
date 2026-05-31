package com.glodblock.github.extendedae.common.items;

import appeng.api.config.FuzzyMode;
import appeng.api.ids.AEComponents;
import appeng.api.implementations.menuobjects.IMenuItem;
import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.api.storage.cells.ICellWorkbenchItem;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.items.AEBaseItem;
import appeng.items.contents.CellConfig;
import appeng.menu.MenuOpener;
import appeng.menu.locator.ItemMenuHostLocator;
import appeng.menu.locator.MenuLocators;
import appeng.util.ConfigInventory;
import com.glodblock.github.extendedae.api.VoidMode;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.common.inventory.VoidCellInventory;
import com.glodblock.github.extendedae.container.ContainerVoidCell;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

public class ItemVoidCell extends AEBaseItem implements ICellWorkbenchItem, IMenuItem {

    public ItemVoidCell(Properties props) {
        super(props.stacksTo(1));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(@NotNull ItemStack is, @NotNull TooltipContext context, @NotNull TooltipDisplay tooltipDisplay, @NotNull Consumer<Component> lines, @NotNull TooltipFlag tooltipFlags) {
        super.appendHoverText(is, context, tooltipDisplay, lines, tooltipFlags);
        VoidCellInventory inv = (VoidCellInventory) VoidCellInventory.HANDLER.getCellInventory(is, null);
        VoidMode mode = is.getOrDefault(EAESingletons.VOID_MODE, VoidMode.TRASH);
        lines.accept(Component.translatable("gui.extendedae.void_cell.mode." + mode.ordinal()).withStyle(ChatFormatting.GREEN));
        if (!(inv != null && inv.isPartitioned())) {
            lines.accept(Component.translatable("void_warn.tooltip").withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack is) {
        VoidCellInventory inv = (VoidCellInventory) VoidCellInventory.HANDLER.getCellInventory(is, null);
        if (inv != null) {
            return inv.getTooltipImage();
        } else {
            return Optional.empty();
        }
    }

    @Override
    public IUpgradeInventory getUpgrades(ItemStack is) {
        return UpgradeInventories.forItem(is, 2);
    }

    @Override
    public ConfigInventory getConfigInventory(ItemStack is) {
        return CellConfig.create(is);
    }

    @Override
    public FuzzyMode getFuzzyMode(ItemStack is) {
        return is.getOrDefault(AEComponents.STORAGE_CELL_FUZZY_MODE, FuzzyMode.IGNORE_ALL);
    }

    @Override
    public void setFuzzyMode(ItemStack is, FuzzyMode fzMode) {
        is.set(AEComponents.STORAGE_CELL_FUZZY_MODE, fzMode);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull Level level, @NotNull Player p, @NotNull InteractionHand hand) {
        if (!level.isClientSide()) {
            MenuOpener.open(ContainerVoidCell.TYPE, p, MenuLocators.forHand(p, hand));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public @Nullable ItemMenuHost<ItemVoidCell> getMenuHost(Player player, ItemMenuHostLocator locator, @Nullable BlockHitResult hitResult) {
        return new ItemMenuHost<>(this, player, locator);
    }

}
