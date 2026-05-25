package com.glodblock.github.extendedae.client.gui;

import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.stacks.GenericStack;
import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ProgressBar;
import appeng.client.gui.widgets.ServerSettingToggleButton;
import appeng.client.gui.widgets.SettingToggleButton;
import appeng.core.localization.Tooltips;
import com.glodblock.github.extendedae.client.button.ActionEPPButton;
import com.glodblock.github.extendedae.client.button.EPPIcon;
import com.glodblock.github.extendedae.client.gui.subgui.OutputSideConfig;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.common.tileentities.TileCrystalAssembler;
import com.glodblock.github.extendedae.container.ContainerCrystalAssembler;
import com.glodblock.github.extendedae.network.EAENetworkHandler;
import com.glodblock.github.extendedae.network.packet.CEAEGenericPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class GuiCrystalAssembler extends UpgradeableScreen<ContainerCrystalAssembler> {

    private final ProgressBar pb;
    private final SettingToggleButton<YesNo> autoExportBtn;
    private final ActionEPPButton outputSideBtn;

    public GuiCrystalAssembler(ContainerCrystalAssembler menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.pb = new ProgressBar(this.menu, style.getImage("progressBar"), ProgressBar.Direction.VERTICAL);
        widgets.add("progressBar", this.pb);
        this.autoExportBtn = new ServerSettingToggleButton<>(Settings.AUTO_EXPORT, YesNo.NO);
        this.addToLeftToolbar(autoExportBtn);
        this.outputSideBtn = new ActionEPPButton(b -> this.openOutputConfig(), EPPIcon.OUTPUT_SIDES);
        this.outputSideBtn.setMessage(Component.translatable("gui.extendedae.set_output_sides.open"));
        this.addToLeftToolbar(this.outputSideBtn);
    }

    private void openOutputConfig() {
        if (this.getMenu().getHost() != null) {
            switchToScreen(new OutputSideConfig<>(
                    this,
                    new ItemStack(EAESingletons.CIRCUIT_CUTTER),
                    this.getMenu().getHost(),
                    this.getMenu().getOutputSides(),
                    (side, value) -> EAENetworkHandler.INSTANCE.sendToServer(new CEAEGenericPacket("set_side", side.getName(), value)))
            );
        }
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        int progress = this.menu.getCurrentProgress() * 100 / this.menu.getMaxProgress();
        this.pb.setFullMsg(Component.literal(progress + "%"));
        this.autoExportBtn.set(getMenu().getAutoExport());
        this.outputSideBtn.setVisibility(this.autoExportBtn.getCurrentValue() == YesNo.YES);
    }

    @Override
    protected void renderTooltip(@NotNull GuiGraphics guiGraphics, int x, int y) {
        if (this.menu.getCarried().isEmpty() && this.isValidSlot(this.hoveredSlot)) {
            var itemTooltip = new ArrayList<>(getTooltipFromContainerItem(this.hoveredSlot.getItem()));
            var unwrapped = GenericStack.fromItemStack(this.hoveredSlot.getItem());
            long amt = unwrapped != null ? unwrapped.amount() : 0;
            itemTooltip.add(Component.translatable("gui.extendedae.crystal_assembler.amount", amt, TileCrystalAssembler.TANK_CAP).withStyle(Tooltips.NORMAL_TOOLTIP_TEXT));
            drawTooltip(guiGraphics, x, y, itemTooltip);
        } else {
            super.renderTooltip(guiGraphics, x, y);
        }
    }

    private boolean isValidSlot(Slot slot) {
        return slot != null && slot.isActive() && slot.hasItem() && this.menu.isTank(slot);
    }

}
