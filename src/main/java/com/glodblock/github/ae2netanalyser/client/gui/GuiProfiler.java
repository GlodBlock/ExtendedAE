package com.glodblock.github.ae2netanalyser.client.gui;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.PaletteColor;
import appeng.client.gui.style.ScreenStyle;
import com.glodblock.github.ae2netanalyser.client.gui.elements.ClickableArea;
import com.glodblock.github.ae2netanalyser.client.gui.elements.ColorArea;
import com.glodblock.github.ae2netanalyser.client.gui.elements.DrawableArea;
import com.glodblock.github.ae2netanalyser.common.items.ItemTickAnalyzer;
import com.glodblock.github.ae2netanalyser.container.ContainerProfiler;
import com.glodblock.github.ae2netanalyser.network.AEANetworkHandler;
import com.glodblock.github.ae2netanalyser.network.packets.CAnalyserGeneric;
import com.glodblock.github.ae2netanalyser.network.packets.CTickConfigSave;
import com.glodblock.github.ae2netanalyser.network.packets.CTickProfilerRequest;
import com.glodblock.github.glodium.client.render.ColorData;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;

public class GuiProfiler extends AEBaseScreen<ContainerProfiler> {

    private static final ColorData RED = new ColorData(1f, 0f, 0f);
    private static final ColorData GREEN = new ColorData(0f, 1f, 0f);
    private int duration = 60;
    private final boolean[] enable = new boolean[4];
    private final ArrayList<ClickableArea> clickables = new ArrayList<>();
    private final EditBox durationInput;
    private final ColorArea[] dots = new ColorArea[4];

    public GuiProfiler(ContainerProfiler menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.durationInput = new EditBox(this.font, 32, 9, Component.empty());
        this.durationInput.setMaxLength(4);
        this.durationInput.setFilter(this::isNumber);
        this.durationInput.setResponder(this::setDuration);
        this.durationInput.setBordered(false);
        this.durationInput.setTextColor(0xFFFFFF);
        this.clickables.add(new ClickableArea(15, 98, 56, 19, this, () -> AEANetworkHandler.INSTANCE.sendToServer(new CTickProfilerRequest(this.duration))));
        this.clickables.add(new ClickableArea(136, 98, 56, 19, this, () -> AEANetworkHandler.INSTANCE.sendToServer(new CTickProfilerRequest(-1))));
        this.clickables.add(this.dots[0] = new ColorArea(83, 47, 4, 4, this, () -> this.cycleEnable(0)));
        this.clickables.add(this.dots[1] = new ColorArea(180, 47, 4, 4, this, () -> this.cycleEnable(1)));
        this.clickables.add(this.dots[2] = new ColorArea(83, 76, 4, 4, this, () -> this.cycleEnable(2)));
        this.clickables.add(this.dots[3] = new ColorArea(180, 76, 4, 4, this, () -> this.cycleEnable(3)));
        AEANetworkHandler.INSTANCE.sendToServer(new CAnalyserGeneric("update"));
    }

    @Override
    protected void init() {
        super.init();
        this.durationInput.setPosition(this.leftPos + 89, this.topPos + 23);
        this.addRenderableWidget(this.durationInput);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        RenderSystem.disableDepthTest();
        for (var c : this.clickables) {
            if (c instanceof DrawableArea d) {
                d.draw(guiGraphics);
            }
        }
        RenderSystem.enableDepthTest();
    }

    @Override
    public boolean mouseClicked(double xCoord, double yCoord, int btn) {
        for (var c : this.clickables) {
            if (c.click(xCoord, yCoord)) {
                return true;
            }
        }
        return super.mouseClicked(xCoord, yCoord, btn);
    }

    @Override
    public void drawFG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {
        var textColor = this.style.getColor(PaletteColor.DEFAULT_TEXT_COLOR).toARGB();
        drawCenteredText(guiGraphics, Component.translatable("gui.ae2netanalyser.tick_analyser.set_duration"), 103, 11, textColor);
        drawCenteredText(guiGraphics, Component.translatable("gui.ae2netanalyser.tick_analyser.begin"), 42, 107, 0xFFFFFFFF);
        drawCenteredText(guiGraphics, Component.translatable("gui.ae2netanalyser.tick_analyser.cancel"), 163, 107, 0xFFFFFFFF);
        drawRightText(guiGraphics, Component.translatable("gui.ae2netanalyser.tick_analyser.range1"), 80, 49, textColor);
        drawRightText(guiGraphics, Component.translatable("gui.ae2netanalyser.tick_analyser.range2"), 177, 49, textColor);
        drawRightText(guiGraphics, Component.translatable("gui.ae2netanalyser.tick_analyser.range3"), 80, 78, textColor);
        drawRightText(guiGraphics, Component.translatable("gui.ae2netanalyser.tick_analyser.range4"), 177, 78, textColor);
    }

    private void drawCenteredText(GuiGraphics guiGraphics, Component text, int centerX, int centerY, int color) {
        int width = this.font.width(text);
        int height = this.font.lineHeight;
        guiGraphics.drawString(this.font, text, centerX - width / 2, centerY - height / 2, color, false);
    }

    private void drawRightText(GuiGraphics guiGraphics, Component text, int rightX, int rightY, int color) {
        int width = this.font.width(text);
        int height = this.font.lineHeight;
        guiGraphics.drawString(this.font, text, rightX - width, rightY - height / 2, color, false);
    }

    private void cycleEnable(int index) {
        this.enable[index] = !this.enable[index];
        this.dots[index].setColor(this.enable[index] ? GREEN : RED);
        AEANetworkHandler.INSTANCE.sendToServer(new CTickConfigSave(new ItemTickAnalyzer.TickConfig(this.duration, this.enable[0], this.enable[1], this.enable[2], this.enable[3])));
    }

    private void setDuration(String text) {
        try {
            this.duration = Integer.parseInt(text);
            if (this.duration <= 0) {
                this.duration = 1;
            }
        } catch (NumberFormatException e) {
            this.duration = 60;
        }
        AEANetworkHandler.INSTANCE.sendToServer(new CTickConfigSave(new ItemTickAnalyzer.TickConfig(this.duration, this.enable[0], this.enable[1], this.enable[2], this.enable[3])));
    }

    private boolean isNumber(String input) {
        for (var c : input.toCharArray()) {
            if (!(c >= '0' && c <= '9')) {
                return false;
            }
        }
        return true;
    }

    public void loadConfig(ItemTickAnalyzer.TickConfig config) {
        this.duration = config.duration();
        this.enable[0] = config.op1();
        this.enable[1] = config.op2();
        this.enable[2] = config.op3();
        this.enable[3] = config.op4();
        this.durationInput.setValue(String.valueOf(this.duration));
        for (int x = 0; x < 4; x++) {
            this.dots[x].setColor(this.enable[x] ? GREEN : RED);
        }
    }

    @Override
    public void onClose() {
        AEANetworkHandler.INSTANCE.sendToServer(new CTickConfigSave(new ItemTickAnalyzer.TickConfig(this.duration, this.enable[0], this.enable[1], this.enable[2], this.enable[3])));
        super.onClose();
    }

}
