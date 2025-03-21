package com.glodblock.github.appflux.common.items;

import appeng.api.config.Actionable;
import appeng.api.implementations.menuobjects.IPortableTerminal;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.MEStorage;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.api.upgrades.Upgrades;
import appeng.items.contents.PortableCellMenuHost;
import appeng.items.tools.powered.AbstractPortableCell;
import appeng.items.tools.powered.powersink.PoweredItemCapabilities;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.me.common.MEStorageMenu;
import com.glodblock.github.appflux.AppFlux;
import com.glodblock.github.appflux.api.IFluxCell;
import com.glodblock.github.appflux.common.AFSingletons;
import com.glodblock.github.appflux.common.me.cell.FECellHandler;
import com.glodblock.github.appflux.common.me.cell.FluxCellInventory;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import com.glodblock.github.appflux.common.me.key.type.EnergyType;
import com.glodblock.github.appflux.util.AFUtil;
import com.google.common.base.Preconditions;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ItemPortableFECell extends AbstractPortableCell implements IFluxCell {

    private final long totalBytes;
    private final double idleDrain;
    private final boolean isMega;
    public static final MenuType<MEStorageMenu> FE_CELL_TYPE = MenuTypeBuilder
            .create(MEStorageMenu::new, IPortableTerminal.class)
            .buildUnregistered(AppFlux.id("portable_fe_cell"));

    public ItemPortableFECell(int quartKilobytes, double idleDrain, int defaultColor) {
        super(FE_CELL_TYPE, new Properties().stacksTo(1), defaultColor);
        this.totalBytes = quartKilobytes * 256L;
        this.idleDrain = idleDrain;
        this.isMega = quartKilobytes >= 1024;
    }

    @Override
    public IUpgradeInventory getUpgrades(ItemStack is) {
        return UpgradeInventories.forItem(is, 4);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof Player player) {
            var induct = this.getUpgrades(stack).isInstalled(AFSingletons.INDUCTION_CARD);
            var storedInv = this.getUsingStorage(player, stack);
            var key = FluxKey.of(EnergyType.FE);
            if (induct && storedInv != null) {
                var inv = player.getInventory();
                for (int slot = 0; slot < inv.getContainerSize(); slot ++) {
                    long stored = storedInv.getAvailableStacks().get(key);
                    if (stored <= 0) {
                        return;
                    }
                    var cap = AFUtil.findCapability(inv.getItem(slot), Capabilities.EnergyStorage.ITEM);
                    if (cap != null && cap.canReceive()) {
                        int toAdd = AFUtil.clampLong(stored);
                        int added = cap.receiveEnergy(toAdd, false);
                        storedInv.extract(key, added, Actionable.MODULATE, IActionSource.ofPlayer(player));
                    }
                }
            }
        }
    }

    private MEStorage getUsingStorage(Player player, ItemStack stack) {
        if (player.containerMenu instanceof MEStorageMenu container) {
            if (container.getHost() instanceof PortableCellMenuHost<?> cell) {
                if (cell.getItemStack() == stack) {
                    return cell.getInventory();
                }
            }
        }
        return FECellHandler.HANDLER.getCellInventory(stack, null);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag advancedTooltips) {
        super.appendHoverText(stack, context, lines, advancedTooltips);
        addCellInformationToTooltip(stack, lines);
    }

    @Override
    public ResourceLocation getRecipeId() {
        return AppFlux.id("tools/" + Objects.requireNonNull(getRegistryName()).getPath());
    }

    @Override
    public double getChargeRate(ItemStack stack) {
        return (80d + 80d * Upgrades.getEnergyCardMultiplier(getUpgrades(stack))) * (this.isMega ? 2 : 1);
    }

    @Override
    public double getAEMaxPower(ItemStack stack) {
        return super.getAEMaxPower(stack) * (this.isMega ? 8 : 1);
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

    // Portable cell's energy storage is its energy bar.
    @Override
    @Nullable
    public IEnergyStorage getCapability(@NotNull ItemStack stack, Void unused) {
        return new PoweredItemCapabilities(stack, this);
    }

}
