package com.glodblock.github.appflux.common.items;

import appeng.api.stacks.AEKeyType;
import appeng.api.storage.StorageCells;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.core.localization.PlayerMessages;
import appeng.items.AEBaseItem;
import appeng.util.InteractionUtil;
import com.glodblock.github.appflux.api.IFluxCell;
import com.glodblock.github.appflux.common.AFItemAndBlock;
import com.glodblock.github.appflux.common.caps.CellFEPower;
import com.glodblock.github.appflux.common.me.cell.FECellHandler;
import com.glodblock.github.appflux.common.me.key.type.EnergyType;
import com.glodblock.github.appflux.common.me.key.type.FluxKeyType;
import com.google.common.base.Preconditions;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;

public class ItemFECell extends AEBaseItem implements IFluxCell {

    private final ItemLike coreItem;
    private final long totalBytes;
    private final double idleDrain;

    public ItemFECell(ItemLike coreItem, int kilobytes, double idleDrain) {
        super(new Properties().stacksTo(1));
        this.coreItem = coreItem;
        this.totalBytes = kilobytes * 1024L;
        this.idleDrain = idleDrain;
    }

    @Override
    public IUpgradeInventory getUpgrades(ItemStack is) {
        return UpgradeInventories.forItem(is, 1);
    }

    @Override
    public AEKeyType getKeyType() {
        return FluxKeyType.TYPE;
    }

    @Override
    public EnergyType getEnergyType() {
        return EnergyType.FE;
    }

    @Override
    public long getBytes(ItemStack cellItem) {
        return this.totalBytes;
    }

    @Override
    public double getIdleDrain() {
        return this.idleDrain;
    }

    @Override
    public void addCellInformationToTooltip(ItemStack is, List<Component> lines) {
        Preconditions.checkArgument(is.getItem() == this);
        FECellHandler.HANDLER.addCellInformationToTooltip(is, lines);
    }

    @Override
    public Optional<TooltipComponent> getCellTooltipImage(ItemStack is) {
        return FECellHandler.HANDLER.getTooltipImage(is);
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack stack) {
        return this.getCellTooltipImage(stack);
    }

    @Override
    @ParametersAreNonnullByDefault
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        this.disassembleDrive(player.getItemInHand(hand), level, player);
        return new InteractionResultHolder<>(InteractionResult.sidedSuccess(level.isClientSide()), player.getItemInHand(hand));
    }

    private boolean disassembleDrive(ItemStack stack, Level level, Player player) {
        if (InteractionUtil.isInAlternateUseMode(player)) {
            if (level.isClientSide()) {
                return false;
            }

            var playerInventory = player.getInventory();
            var inv = StorageCells.getCellInventory(stack, null);

            if (inv != null && playerInventory.getSelected() == stack) {
                var list = inv.getAvailableStacks();
                if (list.isEmpty()) {
                    playerInventory.setItem(playerInventory.selected, ItemStack.EMPTY);
                    playerInventory.placeItemBackInInventory(new ItemStack(this.coreItem));
                    for (var upgrade : this.getUpgrades(stack)) {
                        playerInventory.placeItemBackInInventory(upgrade);
                    }
                    playerInventory.placeItemBackInInventory(new ItemStack(AFItemAndBlock.FE_HOUSING));
                    return true;
                } else {
                    player.displayClientMessage(PlayerMessages.OnlyEmptyCellsCanBeDisassembled.text(), true);
                }
            }
        }
        return false;
    }

    @Override
    public @NotNull InteractionResult onItemUseFirst(@NotNull ItemStack stack, UseOnContext context) {
        return this.disassembleDrive(stack, context.getLevel(), context.getPlayer())
                ? InteractionResult.sidedSuccess(context.getLevel().isClientSide())
                : InteractionResult.PASS;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void appendHoverText(ItemStack is, @Nullable Level level, List<Component> lines, TooltipFlag tooltipFlag) {
        addCellInformationToTooltip(is, lines);
    }

    @Override
    public @Nullable IEnergyStorage getCapability(@NotNull ItemStack stack, @NotNull Void context) {
        var inv = FECellHandler.HANDLER.getCellInventory(stack, null);
        if (inv != null) {
            return new CellFEPower(inv);
        }
        return null;
    }
}
