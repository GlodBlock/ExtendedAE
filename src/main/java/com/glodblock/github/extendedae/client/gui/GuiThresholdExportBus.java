package com.glodblock.github.extendedae.client.gui;

import appeng.api.config.RedstoneMode;
import appeng.api.config.SchedulingMode;
import appeng.api.config.Settings;
import appeng.api.stacks.GenericStack;
import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.PaletteColor;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ServerSettingToggleButton;
import appeng.client.gui.widgets.SettingToggleButton;
import appeng.core.definitions.AEItems;
import appeng.core.localization.ButtonToolTips;
import appeng.core.localization.Tooltips;
import appeng.core.network.serverbound.InventoryActionPacket;
import appeng.helpers.InventoryAction;
import com.glodblock.github.extendedae.api.ThresholdMode;
import com.glodblock.github.extendedae.client.button.CycleEPPButton;
import com.glodblock.github.extendedae.client.button.EPPIcon;
import com.glodblock.github.extendedae.client.gui.subgui.SetAmount;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.container.ContainerThresholdExportBus;
import com.glodblock.github.extendedae.network.EAENetworkHandler;
import com.glodblock.github.extendedae.network.packet.CEAEGenericPacket;
import com.glodblock.github.glodium.network.packet.sync.ActionMap;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class GuiThresholdExportBus extends UpgradeableScreen<ContainerThresholdExportBus> implements IActionHolder {

    private final SettingToggleButton<RedstoneMode> redstoneMode;
    private final SettingToggleButton<SchedulingMode> schedulingMode;
    private final CycleEPPButton thresholdMode;
    private final ActionMap actions = ActionMap.create();

    public GuiThresholdExportBus(ContainerThresholdExportBus menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        this.redstoneMode = new ServerSettingToggleButton<>(Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE);
        addToLeftToolbar(this.redstoneMode);

        if (menu.getHost().getConfigManager().hasSetting(Settings.SCHEDULING_MODE)) {
            this.schedulingMode = new ServerSettingToggleButton<>(Settings.SCHEDULING_MODE, SchedulingMode.DEFAULT);
            addToLeftToolbar(this.schedulingMode);
        } else {
            this.schedulingMode = null;
        }

        this.thresholdMode = new CycleEPPButton();
        this.thresholdMode.addActionPair(EPPIcon.OVER_STACK, Component.translatable("gui.extendedae.threshold_export_bus.greater"), b -> EAENetworkHandler.INSTANCE.sendToServer(new CEAEGenericPacket("set", ThresholdMode.LOWER.ordinal())));
        this.thresholdMode.addActionPair(EPPIcon.BELOW_STACK, Component.translatable("gui.extendedae.threshold_export_bus.lower"), b -> EAENetworkHandler.INSTANCE.sendToServer(new CEAEGenericPacket("set", ThresholdMode.GREATER.ordinal())));
        this.actions.put("init", o -> this.thresholdMode.setState(o.get(0)));
        EAENetworkHandler.INSTANCE.sendToServer(new CEAEGenericPacket("update"));
        addToLeftToolbar(this.thresholdMode);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        this.redstoneMode.set(menu.getRedStoneMode());
        this.redstoneMode.setVisibility(menu.hasUpgrade(AEItems.REDSTONE_CARD));
        if (this.schedulingMode != null) {
            this.schedulingMode.set(menu.getSchedulingMode());
        }
        this.thresholdMode.setState(menu.getMode().ordinal());
    }

    @Override
    public boolean mouseClicked(double xCoord, double yCoord, int btn) {
        assert this.minecraft != null;
        if (this.minecraft.options.keyPickItem.matchesMouse(btn)) {
            var slot = findSlot(xCoord, yCoord);
            if (isValidSlot(slot)) {
                var currentStack = GenericStack.fromItemStack(slot.getItem());
                if (currentStack != null) {
                    var screen = new SetAmount<>(
                            this,
                            new ItemStack(EAESingletons.THRESHOLD_EXPORT_BUS),
                            currentStack,
                            newStack -> PacketDistributor.sendToServer(new InventoryActionPacket(
                                    InventoryAction.SET_FILTER, slot.index,
                                    GenericStack.wrapInItemStack(newStack))),
                            false);
                    switchToScreen(screen);
                    return true;
                }
            }
        }
        return super.mouseClicked(xCoord, yCoord, btn);
    }

    @Override
    protected void renderTooltip(@NotNull GuiGraphics guiGraphics, int x, int y) {
        if (this.menu.getCarried().isEmpty() && this.isValidSlot(this.hoveredSlot)) {
            var itemTooltip = new ArrayList<>(getTooltipFromContainerItem(this.hoveredSlot.getItem()));
            var unwrapped = GenericStack.fromItemStack(this.hoveredSlot.getItem());
            if (unwrapped != null) {
                itemTooltip.add(Tooltips.getAmountTooltip(ButtonToolTips.Amount, unwrapped));
            }
            itemTooltip.add(Tooltips.getSetAmountTooltip());
            drawTooltip(guiGraphics, x, y, itemTooltip);
        } else {
            super.renderTooltip(guiGraphics, x, y);
        }
    }

    @Override
    public void drawFG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {
        super.drawFG(guiGraphics, offsetX, offsetY, mouseX, mouseY);
        var poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(10, 17, 0);
        poseStack.scale(0.6f, 0.6f, 1);
        var color = style.getColor(PaletteColor.DEFAULT_TEXT_COLOR);
        guiGraphics.drawString(font, Component.translatable("gui.extendedae.precise_export_bus.set_amount"), 0, 0, color.toARGB(), false);
        poseStack.popPose();
    }

    private boolean isValidSlot(Slot slot) {
        return slot != null && slot.isActive() && slot.hasItem() && this.menu.isConfigSlot(slot);
    }

    @NotNull
    @Override
    public ActionMap getActionMap() {
        return this.actions;
    }
}
