package com.github.glodblock.epp.container;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.stacks.GenericStack;
import appeng.core.definitions.AEItems;
import appeng.crafting.pattern.AEProcessingPattern;
import appeng.crafting.pattern.EncodedPatternItem;
import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.slot.AppEngSlot;
import appeng.menu.slot.OutputSlot;
import appeng.menu.slot.RestrictedInputSlot;
import com.github.glodblock.epp.api.IPage;
import com.github.glodblock.epp.client.ExSemantics;
import com.github.glodblock.epp.common.inventory.PatternModifierInventory;
import com.github.glodblock.epp.network.packet.sync.IActionHolder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;

public class ContainerPatternModifier extends AEBaseMenu implements IPage, IActionHolder {

    private final Map<String, Consumer<Object[]>> actions = new Object2ObjectOpenHashMap<>();
    public static final MenuType<ContainerPatternModifier> TYPE = MenuTypeBuilder
            .create(ContainerPatternModifier::new, PatternModifierInventory.class)
            .build("pattern_modifier");

    public final AppEngSlot targetSlot;
    public final AppEngSlot cloneSlot;
    @GuiSync(1)
    public int page;

    public ContainerPatternModifier(int id, Inventory playerInventory, PatternModifierInventory host) {
        super(TYPE, id, playerInventory, host);
        this.createPlayerInventorySlots(playerInventory);
        var patternInv = host.getInventoryByName("patternInv");
        for (int x = 0; x < patternInv.size(); x ++) {
            this.addSlot(new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.ENCODED_PATTERN, patternInv, x), SlotSemantics.ENCODED_PATTERN);
        }
        var targetInv = host.getInventoryByName("targetInv");
        var blankPatternInv = host.getInventoryByName("blankPatternInv");
        var clonePatternInv = host.getInventoryByName("clonePatternInv");
        this.targetSlot = (AppEngSlot) this.addSlot(new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.ENCODED_PATTERN, targetInv, 0), ExSemantics.EX_1);
        this.cloneSlot = (AppEngSlot) this.addSlot(new OutputSlot(clonePatternInv, 0, null), ExSemantics.EX_2);
        for (int x = 0; x < blankPatternInv.size(); x ++) {
            this.addSlot(new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.BLANK_PATTERN, blankPatternInv, x), ExSemantics.EX_3);
        }
        this.actions.put("clear", o -> clear());
        this.actions.put("clone", o -> clonePattern());
        this.actions.put("modify", o -> modify((int) o[0], (int) o[1] == 0));
    }

    public void showPage(int page) {
        for (var slot : this.getSlots(SlotSemantics.ENCODED_PATTERN)) {
            if (slot instanceof AppEngSlot as) {
                as.setActive(page == 0);
            }
        }
        this.targetSlot.setActive(page == 1);
        this.cloneSlot.setActive(page == 1);
        for (var slot : this.getSlots(ExSemantics.EX_3)) {
            if (slot instanceof AppEngSlot as) {
                as.setActive(page == 1);
            }
        }
    }

    public void clear() {
        for (var slot : this.getSlots(SlotSemantics.ENCODED_PATTERN)) {
            var stack = slot.getItem();
            if (stack.getItem() instanceof EncodedPatternItem) {
                slot.set(AEItems.BLANK_PATTERN.stack());
            }
        }
    }

    public void clonePattern() {
        var target = this.targetSlot.getItem();
        var clone = this.cloneSlot.getItem();
        if (target.getItem() instanceof EncodedPatternItem pattern) {
            var detail = pattern.decode(target, this.getPlayer().level(), false);
            if (detail != null) {
                var newPattern = target.copy();
                if (clone.isEmpty()) {
                    if (consumeBlankPattern()) {
                        this.cloneSlot.set(newPattern);
                    }
                } else if (clone.getItem() instanceof EncodedPatternItem) {
                    this.cloneSlot.set(newPattern);
                }
            }
        }
    }

    public void modify(int scale, boolean div) {
        if (scale <= 0) {
            return;
        }
        for (var slot : this.getSlots(SlotSemantics.ENCODED_PATTERN)) {
            var stack = slot.getItem();
            if (stack.getItem() instanceof EncodedPatternItem pattern) {
                var detail = pattern.decode(stack, this.getPlayer().level(), false);
                if (detail instanceof AEProcessingPattern process) {
                    var input = process.getSparseInputs();
                    var output = process.getOutputs();
                    if (checkModify(input, scale, div) && checkModify(output, scale, div)) {
                        var mulInput = new GenericStack[input.length];
                        var mulOutput = new GenericStack[output.length];
                        modifyStacks(input, mulInput, scale, div);
                        modifyStacks(output, mulOutput, scale, div);
                        var newPattern = PatternDetailsHelper.encodeProcessingPattern(mulInput, mulOutput);
                        slot.set(newPattern);
                    }
                }
            }
        }
    }

    private boolean checkModify(GenericStack[] stacks, int scale, boolean div) {
        if (div) {
            for (var stack : stacks) {
                if (stack != null) {
                    if (stack.amount() % scale != 0) {
                        return false;
                    }
                }
            }
        } else {
            for (var stack : stacks) {
                if (stack != null) {
                    long upper = 999999L * stack.what().getAmountPerUnit();
                    if (stack.amount() * scale > upper) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void modifyStacks(GenericStack[] stacks, GenericStack[] des, int scale, boolean div) {
        for (int i = 0; i < stacks.length; i ++) {
            if (stacks[i] != null) {
                long amt = div ? stacks[i].amount() / scale : stacks[i].amount() * scale;
                des[i] = new GenericStack(stacks[i].what(), amt);
            }
        }
    }

    private boolean consumeBlankPattern() {
        for (var slot : this.getSlots(ExSemantics.EX_3)) {
            var stack = slot.getItem();
            if (!stack.isEmpty() && AEItems.BLANK_PATTERN.isSameAs(stack)) {
                stack.shrink(1);
                if (stack.getCount() <= 0) {
                    slot.set(ItemStack.EMPTY);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public int getPage() {
        return this.page;
    }

    @NotNull
    @Override
    public Map<String, Consumer<Object[]>> getActionMap() {
        return this.actions;
    }
}
