package com.glodblock.github.extendedae.common.me.itemhost;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.menu.locator.ItemMenuHostLocator;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.InternalInventoryHost;
import com.glodblock.github.extendedae.common.items.tools.ItemPatternModifier;
import net.minecraft.world.entity.player.Player;

public class HostPatternModifier extends ItemMenuHost<ItemPatternModifier> implements InternalInventoryHost {

    private final AppEngInternalInventory patternInv = new AppEngInternalInventory(this, 27);
    private final AppEngInternalInventory targetInv = new AppEngInternalInventory(this, 1);
    private final AppEngInternalInventory blankPatternInv = new AppEngInternalInventory(this, 4);
    private final AppEngInternalInventory clonePatternInv = new AppEngInternalInventory(this, 1);
    private final AppEngInternalInventory replaceInv = new AppEngInternalInventory(this, 2);

    public HostPatternModifier(ItemPatternModifier item, Player player, ItemMenuHostLocator locator) {
        super(item, player, locator);
        var itemTag = this.getItemStack().getTag();
        if (itemTag != null) {
            this.patternInv.readFromNBT(itemTag, "patternInv");
            this.targetInv.readFromNBT(itemTag, "targetInv");
            this.blankPatternInv.readFromNBT(itemTag, "blankPatternInv");
            this.clonePatternInv.readFromNBT(itemTag, "clonePatternInv");
            this.replaceInv.readFromNBT(itemTag, "replaceInv");
        }
    }

    @Override
    public void saveChangedInventory(AppEngInternalInventory inv) {
        var itemTag = this.getItemStack().getOrCreateTag();
        this.patternInv.writeToNBT(itemTag, "patternInv");
        this.targetInv.writeToNBT(itemTag, "targetInv");
        this.blankPatternInv.writeToNBT(itemTag, "blankPatternInv");
        this.clonePatternInv.writeToNBT(itemTag, "clonePatternInv");
        this.replaceInv.writeToNBT(itemTag, "replaceInv");
    }

    @Override
    public void onChangeInventory(AppEngInternalInventory inv, int slot) {
        var itemTag = this.getItemStack().getOrCreateTag();
        if (this.patternInv == inv) {
            this.patternInv.writeToNBT(itemTag, "patternInv");
        }
        if (this.targetInv == inv) {
            this.targetInv.writeToNBT(itemTag, "targetInv");
        }
        if (this.blankPatternInv == inv) {
            this.blankPatternInv.writeToNBT(itemTag, "blankPatternInv");
        }
        if (this.clonePatternInv == inv) {
            this.clonePatternInv.writeToNBT(itemTag, "clonePatternInv");
        }
        if (this.replaceInv == inv) {
            this.replaceInv.writeToNBT(itemTag, "replaceInv");
        }
    }

    public AppEngInternalInventory getInventoryByName(String name) {
        return switch (name) {
            case "patternInv" -> this.patternInv;
            case "targetInv" -> this.targetInv;
            case "blankPatternInv" -> this.blankPatternInv;
            case "clonePatternInv" -> this.clonePatternInv;
            case "replaceInv" -> this.replaceInv;
            default -> null;
        };
    }

}
