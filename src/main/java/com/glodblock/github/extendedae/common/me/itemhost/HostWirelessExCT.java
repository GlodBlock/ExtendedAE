package com.glodblock.github.extendedae.common.me.itemhost;

import appeng.api.inventories.InternalInventory;
import appeng.helpers.WirelessTerminalMenuHost;
import appeng.menu.ISubMenu;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.InternalInventoryHost;
import com.glodblock.github.extendedae.api.CraftingMode;
import com.glodblock.github.extendedae.common.parts.PartExCraftingTerminal;
import com.glodblock.github.extendedae.util.helper.ExtendedCraftingTerminal;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public class HostWirelessExCT extends WirelessTerminalMenuHost implements ExtendedCraftingTerminal, InternalInventoryHost {

    private CraftingMode currentMode;
    @Nullable
    private ResourceLocation stonecuttingRecipe = null;
    private final AppEngInternalInventory craftingGrid = new AppEngInternalInventory(this, 9);
    private final AppEngInternalInventory stonecuttingGrid = new AppEngInternalInventory(this, 1);
    private final AppEngInternalInventory smithingGrid = new AppEngInternalInventory(this, 3);
    private final AppEngInternalInventory anvilGrid = new AppEngInternalInventory(this, 2);

    public HostWirelessExCT(Player player, @Nullable Integer slot, ItemStack itemStack, BiConsumer<Player, ISubMenu> returnToMainMenu) {
        super(player, slot, itemStack, returnToMainMenu);
        var tag = this.getItemStack().getOrCreateTag();
        this.currentMode = CraftingMode.fromOrdinal(tag.getInt("craftingMode"));
        this.craftingGrid.readFromNBT(tag, "craftingGrid");
        this.stonecuttingGrid.readFromNBT(tag, "stonecuttingGrid");
        this.smithingGrid.readFromNBT(tag, "smithingGrid");
        this.anvilGrid.readFromNBT(tag, "anvilGrid");
        if (tag.contains("stonecuttingRecipe")) {
            this.stonecuttingRecipe = ResourceLocation.tryParse(tag.getString("stonecuttingRecipe"));
        }
    }

    @Override
    public CraftingMode getCurrentMode() {
        return this.currentMode;
    }

    @Override
    public @Nullable ResourceLocation getStonecuttingRecipe() {
        return this.stonecuttingRecipe;
    }

    @Override
    public void setCurrentMode(CraftingMode mode) {
        this.currentMode = mode;
        this.saveChanges();
    }

    @Override
    public void setStonecuttingRecipe(ResourceLocation stonecuttingRecipe) {
        this.stonecuttingRecipe = stonecuttingRecipe;
        this.saveChanges();
    }

    @Override
    public @Nullable InternalInventory getSubInventory(ResourceLocation id) {
        if (id.equals(PartExCraftingTerminal.INV_CRAFTING)) {
            return this.craftingGrid;
        }
        if (id.equals(PartExCraftingTerminal.INV_STONECUTTING)) {
            return this.stonecuttingGrid;
        }
        if (id.equals(PartExCraftingTerminal.INV_SMITHING)) {
            return this.smithingGrid;
        }
        if (id.equals(PartExCraftingTerminal.INV_ANVIL)) {
            return this.anvilGrid;
        }
        return null;
    }

    @Override
    public void saveChanges() {
        var tag = this.getItemStack().getOrCreateTag();
        tag.putInt("craftingMode", currentMode.ordinal());
        this.craftingGrid.writeToNBT(tag, "craftingGrid");
        this.stonecuttingGrid.writeToNBT(tag, "stonecuttingGrid");
        this.smithingGrid.writeToNBT(tag, "smithingGrid");
        this.anvilGrid.writeToNBT(tag, "anvilGrid");
        if (this.stonecuttingRecipe != null) {
            tag.putString("stonecuttingRecipe", this.stonecuttingRecipe.toString());
        }
    }

    @Override
    public void onChangeInventory(InternalInventory inv, int slot) {
        this.saveChanges();
    }

}
