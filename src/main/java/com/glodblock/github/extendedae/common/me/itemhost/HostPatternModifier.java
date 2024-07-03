package com.glodblock.github.extendedae.common.me.itemhost;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.menu.locator.ItemMenuHostLocator;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.InternalInventoryHost;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.common.items.tools.ItemPatternModifier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class HostPatternModifier extends ItemMenuHost<ItemPatternModifier> implements InternalInventoryHost {

    private final AppEngInternalInventory patternInv = new AppEngInternalInventory(this, 27);
    private final AppEngInternalInventory targetInv = new AppEngInternalInventory(this, 1);
    private final AppEngInternalInventory blankPatternInv = new AppEngInternalInventory(this, 4);
    private final AppEngInternalInventory clonePatternInv = new AppEngInternalInventory(this, 1);
    private final AppEngInternalInventory replaceInv = new AppEngInternalInventory(this, 2);

    public HostPatternModifier(ItemPatternModifier item, Player player, ItemMenuHostLocator locator) {
        super(item, player, locator);
        var itemTag = this.getItemStack().get(EAESingletons.STACK_TAG);
        var registry = player.registryAccess();
        if (itemTag != null) {
            this.patternInv.readFromNBT(itemTag, "patternInv", registry);
            this.targetInv.readFromNBT(itemTag, "targetInv", registry);
            this.blankPatternInv.readFromNBT(itemTag, "blankPatternInv", registry);
            this.clonePatternInv.readFromNBT(itemTag, "clonePatternInv", registry);
            this.replaceInv.readFromNBT(itemTag, "replaceInv", registry);
        }
    }

    @Override
    public void saveChangedInventory(AppEngInternalInventory inv) {
        var itemTag = new CompoundTag();
        var registry = this.getPlayer().registryAccess();
        this.patternInv.writeToNBT(itemTag, "patternInv", registry);
        this.targetInv.writeToNBT(itemTag, "targetInv", registry);
        this.blankPatternInv.writeToNBT(itemTag, "blankPatternInv", registry);
        this.clonePatternInv.writeToNBT(itemTag, "clonePatternInv", registry);
        this.replaceInv.writeToNBT(itemTag, "replaceInv", registry);
        if (!itemTag.isEmpty()) {
            this.getItemStack().set(EAESingletons.STACK_TAG, itemTag);
        } else {
            this.getItemStack().remove(EAESingletons.STACK_TAG);
        }
    }

    @Override
    public void onChangeInventory(AppEngInternalInventory inv, int slot) {
        var itemTag = this.getItemStack().getOrDefault(EAESingletons.STACK_TAG, new CompoundTag());
        var registry = this.getPlayer().registryAccess();
        if (this.patternInv == inv) {
            this.patternInv.writeToNBT(itemTag, "patternInv", registry);
        }
        if (this.targetInv == inv) {
            this.targetInv.writeToNBT(itemTag, "targetInv", registry);
        }
        if (this.blankPatternInv == inv) {
            this.blankPatternInv.writeToNBT(itemTag, "blankPatternInv", registry);
        }
        if (this.clonePatternInv == inv) {
            this.clonePatternInv.writeToNBT(itemTag, "clonePatternInv", registry);
        }
        if (this.replaceInv == inv) {
            this.replaceInv.writeToNBT(itemTag, "replaceInv", registry);
        }
        if (!itemTag.isEmpty()) {
            this.getItemStack().set(EAESingletons.STACK_TAG, itemTag);
        } else {
            this.getItemStack().remove(EAESingletons.STACK_TAG);
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
