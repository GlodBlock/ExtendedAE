package com.glodblock.github.extendedae.common.me.itemhost;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.menu.locator.ItemMenuHostLocator;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.InternalInventoryHost;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.common.items.tools.ItemPatternModifier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;

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
            try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(ExtendedAE.LOGGER)) {
                var input = TagValueInput.create(reporter, registry, itemTag);
                this.patternInv.readFromNBT(input, "patternInv");
                this.targetInv.readFromNBT(input, "targetInv");
                this.blankPatternInv.readFromNBT(input, "blankPatternInv");
                this.clonePatternInv.readFromNBT(input, "clonePatternInv");
                this.replaceInv.readFromNBT(input, "replaceInv");
            }
        }
    }

    @Override
    public void saveChangedInventory(AppEngInternalInventory inv) {
        var registry = this.getPlayer().registryAccess();
        try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(ExtendedAE.LOGGER)) {
            var output = TagValueOutput.createWithContext(reporter, registry);
            this.patternInv.writeToNBT(output, "patternInv");
            this.targetInv.writeToNBT(output, "targetInv");
            this.blankPatternInv.writeToNBT(output, "blankPatternInv");
            this.clonePatternInv.writeToNBT(output, "clonePatternInv");
            this.replaceInv.writeToNBT(output, "replaceInv");
            var itemTag = output.buildResult();
            if (!itemTag.isEmpty()) {
                this.getItemStack().set(EAESingletons.STACK_TAG, itemTag);
            } else {
                this.getItemStack().remove(EAESingletons.STACK_TAG);
            }
        }
    }

    @Override
    public void onChangeInventory(AppEngInternalInventory inv, int slot) {
        var itemTag = this.getItemStack().getOrDefault(EAESingletons.STACK_TAG, new CompoundTag());
        var registry = this.getPlayer().registryAccess();
        try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(ExtendedAE.LOGGER)) {
            var output = TagValueOutput.createWithContext(reporter, registry);
            if (this.patternInv == inv) {
                this.patternInv.writeToNBT(output, "patternInv");
            }
            if (this.targetInv == inv) {
                this.targetInv.writeToNBT(output, "targetInv");
            }
            if (this.blankPatternInv == inv) {
                this.blankPatternInv.writeToNBT(output, "blankPatternInv");
            }
            if (this.clonePatternInv == inv) {
                this.clonePatternInv.writeToNBT(output, "clonePatternInv");
            }
            if (this.replaceInv == inv) {
                this.replaceInv.writeToNBT(output, "replaceInv");
            }
            itemTag.merge(output.buildResult());
            if (!itemTag.isEmpty()) {
                this.getItemStack().set(EAESingletons.STACK_TAG, itemTag);
            } else {
                this.getItemStack().remove(EAESingletons.STACK_TAG);
            }
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
