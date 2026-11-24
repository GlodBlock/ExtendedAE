package com.glodblock.github.ae2netanalyser.client.gui;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.PaletteColor;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.AETextField;
import com.glodblock.github.ae2netanalyser.client.gui.buttons.ColorButton;
import com.glodblock.github.ae2netanalyser.client.gui.buttons.TextButton;
import com.glodblock.github.ae2netanalyser.client.gui.textures.Blitters;
import com.glodblock.github.ae2netanalyser.common.items.ItemTickAnalyzer;
import com.glodblock.github.ae2netanalyser.container.ContainerProfiler;
import com.glodblock.github.ae2netanalyser.network.AEANetworkHandler;
import com.glodblock.github.ae2netanalyser.network.packets.CTickConfigSave;
import com.glodblock.github.ae2netanalyser.network.packets.CTickProfilerRequest;
import com.glodblock.github.glodium.client.render.ColorData;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.stream.IntStream;

public class GuiProfiler extends AEBaseScreen<ContainerProfiler> {

    private static final ColorData RED = new ColorData(1f, 0f, 0f);
    private static final ColorData GREEN = new ColorData(0f, 1f, 0f);
    private int duration = 60;
    private final boolean[] enable = new boolean[4];
    private final AETextField durationInput;

    public GuiProfiler(ContainerProfiler menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.durationInput = this.widgets.addTextField("duration");
        this.durationInput.setMaxLength(4);
        this.durationInput.setFilter(this::isNumber);
        this.durationInput.setResponder(this::setDuration);
        IntStream.range(0, 4).forEach(i -> this.widgets.add("range" + (i + 1), new ColorButton(() -> this.cycleEnable(i), () -> this.getColor(i))));
        var background = new TextButton.Background(Blitters.BUTTON, Blitters.BUTTON_HOVER, Blitters.BUTTON_SELECT);
        this.widgets.add("start", new TextButton(background, Component.translatable("gui.ae2netanalyser.tick_analyser.begin"), () -> AEANetworkHandler.INSTANCE.sendToServer(new CTickProfilerRequest(this.duration))));
        this.widgets.add("abort", new TextButton(background, Component.translatable("gui.ae2netanalyser.tick_analyser.cancel"), () -> AEANetworkHandler.INSTANCE.sendToServer(new CTickProfilerRequest(-1))));
        this.loadConfig(menu.initConfig);
    }

    private ColorData getColor(int slot) {
        return this.enable[slot] ? GREEN : RED;
    }

    @Override
    public void drawFG(GuiGraphicsExtractor guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {
        var textColor = this.style.getColor(PaletteColor.DEFAULT_TEXT_COLOR).toARGB();
        drawCenteredText(guiGraphics, Component.translatable("gui.ae2netanalyser.tick_analyser.set_duration"), 103, 11, textColor);
        drawRightText(guiGraphics, Component.translatable("gui.ae2netanalyser.tick_analyser.range1"), 80, 49, textColor);
        drawRightText(guiGraphics, Component.translatable("gui.ae2netanalyser.tick_analyser.range2"), 177, 49, textColor);
        drawRightText(guiGraphics, Component.translatable("gui.ae2netanalyser.tick_analyser.range3"), 80, 78, textColor);
        drawRightText(guiGraphics, Component.translatable("gui.ae2netanalyser.tick_analyser.range4"), 177, 78, textColor);
    }

    private void drawCenteredText(GuiGraphicsExtractor guiGraphics, Component text, int centerX, int centerY, int color) {
        int width = this.font.width(text);
        int height = this.font.lineHeight;
        guiGraphics.text(this.font, text, centerX - width / 2, centerY - height / 2, color, false);
    }

    private void drawRightText(GuiGraphicsExtractor guiGraphics, Component text, int rightX, int rightY, int color) {
        int width = this.font.width(text);
        int height = this.font.lineHeight;
        guiGraphics.text(this.font, text, rightX - width, rightY - height / 2, color, false);
    }

    private void cycleEnable(int index) {
        this.enable[index] = !this.enable[index];
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
    }

    @Override
    public void onClose() {
        AEANetworkHandler.INSTANCE.sendToServer(new CTickConfigSave(new ItemTickAnalyzer.TickConfig(this.duration, this.enable[0], this.enable[1], this.enable[2], this.enable[3])));
        super.onClose();
    }

}
