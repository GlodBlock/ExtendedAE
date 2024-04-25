package com.github.glodblock.extendedae.client.gui.subgui;

import appeng.api.stacks.GenericStack;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.AESubScreen;
import appeng.client.gui.NumberEntryType;
import appeng.client.gui.me.common.ClientDisplaySlot;
import appeng.client.gui.widgets.NumberEntryWidget;
import appeng.client.gui.widgets.TabButton;
import appeng.core.localization.GuiText;
import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantics;
import com.google.common.primitives.Longs;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public class SetAmount<C extends AEBaseMenu, P extends AEBaseScreen<C>> extends AESubScreen<C, P> {
    private final NumberEntryWidget amount;

    private final GenericStack currentStack;

    private final Consumer<GenericStack> setter;
    private final boolean checkSize;

    public SetAmount(P parentScreen, ItemStack icon, GenericStack currentStack, Consumer<GenericStack> setter) {
        this(parentScreen, icon, currentStack, setter, true);
    }

    public SetAmount(P parentScreen, ItemStack icon, GenericStack currentStack, Consumer<GenericStack> setter, boolean checkSize) {
        super(parentScreen, "/screens/set_precise_bus_amount.json");
        this.checkSize = checkSize;
        this.currentStack = currentStack;
        this.setter = setter;
        this.widgets.addButton("save", GuiText.Set.text(), this::confirm);

        var button = new TabButton(icon, icon.getHoverName(), btn -> returnToParent());
        this.widgets.add("back", button);

        this.amount = widgets.addNumberEntryWidget("amountToStock", NumberEntryType.of(currentStack.what()));
        this.amount.setLongValue(currentStack.amount());
        if (this.checkSize) {
            this.amount.setMaxValue(getMaxAmount());
        }
        this.amount.setTextFieldStyle(style.getWidget("amountToStockInput"));
        this.amount.setMinValue(0);
        this.amount.setHideValidationIcon(true);
        this.amount.setOnConfirm(this::confirm);
        addClientSideSlot(new ClientDisplaySlot(currentStack), SlotSemantics.MACHINE_OUTPUT);
    }

    @Override
    protected void init() {
        super.init();
        setSlotsHidden(SlotSemantics.TOOLBOX, true);
    }

    @SuppressWarnings("UnstableApiUsage")
    private void confirm() {
        this.amount.getLongValue().ifPresent(newAmount -> {
            if (this.checkSize) {
                newAmount = Longs.constrainToRange(newAmount, 0, getMaxAmount());
            } else {
                newAmount = Math.max(newAmount, 0);
            }
            if (newAmount <= 0) {
                setter.accept(null);
            } else {
                setter.accept(new GenericStack(currentStack.what(), newAmount));
            }
            returnToParent();
        });
    }

    private long getMaxAmount() {
        return 64L * (long) currentStack.what().getAmountPerUnit();
    }
}